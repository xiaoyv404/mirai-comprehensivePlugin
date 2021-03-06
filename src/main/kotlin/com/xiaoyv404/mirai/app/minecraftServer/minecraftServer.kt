package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.databace.dao.*
import com.xiaoyv404.mirai.tool.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import org.apache.commons.cli.*
import java.util.*

@ExperimentalSerializationApi
@App
class MinecraftServerStats : NfApp(), IFshApp {
    override fun getAppName() = "MinecraftServerStats"
    override fun getVersion() = "1.0.2"
    override fun getAppDescription() = "我的世界服务器状态监测"
    override fun getCommands() =
        arrayOf("-服务器熟了没", "-服务器状态", "-土豆熟了没", "-土豆状态", "-破推头熟了没", "-破推头状态", "-ServerStatus", "-PotatoStatus")


    private val options = Options().apply {
        addOption("p", "player", false, "获取玩家列表")
    }

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        val cmdLine = IFshApp.cmdLine(options, args)
        MinecraftServerMap {
            groupID = msg.gid()
        }.findByGroupId().forEach { si ->
            val info = MinecraftServer {
                id = si.serverID
            }.findById()
            if (info != null) {
                sendInfo(
                    msg, info,
                    cmdLine.hasOption("player")
                )
            }
        }
        return true
    }

    override fun init() {
        timer.schedule(task, Date(), 60000)
    }

    override fun uninit() {
        timer.cancel()
    }

    private val timer = Timer()
    private val task = object : TimerTask() {
        override fun run() {
            PluginMain.launch {
                getAll().forEach {
                    check(it)
                }
            }
        }
    }

    private suspend fun sendInfo(msg: MessageEvent, info: MinecraftServer, playerList: Boolean = false) {
        val infoD = getServerInfo(info.host, info.port)
        val bot = msg.bot
        val players = infoD.serverInformationFormat?.players

        //判断当前服务器状态
        val statusT = if (infoD.status != 1)
            -1
        else
            1

        //通过状态生成提示语
        val msgA =
            if (statusT == 1)
                msg.subject.uploadImage(
                    players.let {
                        MinecraftDataImgGenerator.getImg(
                            it!!.players,
                            "${it.online}/${it.max}",
                            info.host,
                            info.port.toString()
                        )
                    }
                ).toMessageChain()
            else
                PlainText(
                    """
                    :(
                    ${info.name} is Offline
                    IP: ${info.host}:${info.port}
                    """.trimIndent()
                )

        //获取服务器关联群，并发送提示
        if (statusT != info.status) {
            MinecraftServerMap {
                serverID = info.id
            }.findByServerId().forEach {
                (bot.getGroup(it.groupID) ?: return@forEach).sendMessage(msgA)
            }
            MinecraftServer {
                id = info.id
                status = statusT
            }.update()
        } else
            msg.reply(msgA, false)

        //如果有需求并且服务器在线，发送玩家列表
        if (statusT == 1 && playerList) {
            sendPlayerList(msg, getPlayerList(info.host, info.port, players!!))
        }
    }

    private suspend fun getPlayerList(host: String, port: Int, players: Players): List<Player> {
        val playersML = players.players.toMutableList()
        var cycles = players.online / 12
        if (cycles != 0)
            cycles++

        log.info("尝试获取PlayList次数：${cycles + 1}次")

        for (i in 1..cycles) {
            getServerInfo(host, port).serverInformationFormat?.players?.players?.forEach {
                playersML.add(it)
            }
        }

        val playersL = playersML.distinct()
        log.info("已获取到玩家列表人数：${playersL.size}")
        return playersL
    }

    private suspend fun sendPlayerList(msg: MessageEvent, players: List<Player>) {
        if (players.isEmpty())
            msg.reply("都没有玩家怎么播报列表啊（恼）", quote = true)
        else
            msg.reply(
                buildForwardMessage(msg.subject) {
                    players.forEach { player ->
                        msg.subject.bot.says(
                            """
                        name: ${player.name}
                        d: ${player.id}
                        """.trimIndent()
                        )
                    }
                }.toMessageChain(), quote = false
            )
    }

    suspend fun check(info: MinecraftServer) {
        PluginMain.launch {
            val information = getServerInfo(info.host, info.port)
            val groups = mutableListOf<Contact>()
            val bot = Bot.getInstanceOrNull(2079373402) ?: return@launch

            val statusD = information.status
            val players = information.serverInformationFormat?.players

            //判断服务器现在是什么状态
            val statusT = if (statusD != 1)
                if (info.status == 1)
                    0
                else
                    -1
            else
                1

            //发送log并获取服务器的关联群
            if ((statusT == -1 && info.status != -1) || (statusT == 1 && info.status == -1)) {
                if (statusT == 1)
                    log.info("服务器 ${info.name} 上线")
                else
                    log.info("服务器 ${info.name} 离线")
                MinecraftServerMap { serverID = info.id }.findByServerId().forEach {
                    groups.add(bot.getGroup(it.groupID) ?: return@forEach)
                }
            }

            //更新数据库内状态
            if (statusT != info.status) {
                MinecraftServer {
                    id = info.id
                    status = statusT
                }.update()
            }

            //发送状态提示
            groups.forEach {
                if (statusT == 1)
                    players?.let { it1 ->
                        it.sendImage(
                            MinecraftDataImgGenerator.getImg(
                                it1.players,
                                "${players.online}/${players.max}",
                                info.host,
                                info.port.toString()
                            )
                        )
                    }
                else
                    it.sendMessage(
                        """
                    :(
                    ${info.name} is Offline
                    IP: ${info.host}:${info.port}
                    """.trimIndent()
                    )
            }
        }
    }

    private suspend fun getServerInfo(host: String, port: Int): ServerInformationFormatAndStatus {
        val pJ = ServerInformationFormatAndStatus()
        return try {
            pJ.serverInformationFormat = Json.decodeFromString(
                ClientUtils.get(
                    "http://127.0.0.1:8080/server?" +
                        "host=$host&" +
                        "port=$port"
                )
            )
            pJ
        } catch (e: Exception) {
            PluginMain.logger.debug(e.message)
            pJ.status = 0
            pJ
        }
    }
}