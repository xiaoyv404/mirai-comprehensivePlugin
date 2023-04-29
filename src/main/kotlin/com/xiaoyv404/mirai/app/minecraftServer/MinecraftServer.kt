package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.dao.*
import com.xiaoyv404.mirai.model.mincraftServer.*
import com.xiaoyv404.mirai.tool.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.code.*
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
        arrayOf(
            "-服务器熟了没",
            "-服务器状态",
            "-土豆熟了没",
            "-土豆状态",
            "-破推头熟了没",
            "-破推头状态",
            "-ServerStatus",
            "-PotatoStatus",
        )

    override fun getOptions(): Options = NfOptions().apply {
        addOption("p", "player", false, "获取玩家列表")
        addOption("s", "server", true, "选择服务器")
    }

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        val cmdLine = IFshApp.cmdLine(getOptions(), args)

        val info = when {
            cmdLine.hasOption("server") -> cmdLine.getOptionValue("server")
            msg.gid() == 113594190L -> "gtnh"
            else -> "MCG"
        }.findByName() ?: return false

        sendInfo(
            msg, info,
            cmdLine.hasOption("player")
        )
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
                    PluginMain.launch {
                        check(it)
                    }
                }
            }
        }
    }

    private suspend fun sendInfo(msg: MessageEvent, server: MinecraftServer, playerList: Boolean = false) {
        val info = getServerInfo(server.host, server.port)
        val bot = msg.bot
        val players = info.serverInformationFormat?.players

        //判断当前服务器状态
        val statusT = if (info.status != 1)
            -1
        else
            1

        //通过状态生成提示语
        val data = server.msgMaker(statusT, players, msg.subject)


        //获取服务器关联群，并发送提示
        if (statusT != server.status) {
            MinecraftServerMap {
                serverID = server.id
            }.findByServerId().forEach {
                (bot.getGroup(it.groupID) ?: return@forEach).sendMessage(data)
            }
        } else
            msg.reply(data, false)

        MinecraftServer {
            id = server.id
            status = statusT
            playerNum = players?.online ?: 0
            playerMaxNum = players?.max ?: server.playerMaxNum
        }.update()

        //如果有需求并且服务器在线，发送玩家列表
        //并更新在线玩家列表
        if (statusT == 1 && playerList)
            getPlayerList(server.host, server.port, players!!).let {
                it.save(server.name)
                server.getOnlinePlayers().send(msg)
            }
        else
            players?.players?.save(server.name)

    }

    suspend fun check(info: MinecraftServer) {
        val information = getServerInfo(info.host, info.port)
        val groups = mutableListOf<Contact>()
        val bot = Bot.getInstanceOrNull(2079373402) ?: return

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

        //更新在线玩家列表
        players?.players?.save(info.name)

        //更新数据库内状态
        MinecraftServer {
            id = info.id
            status = statusT
            playerNum = information.serverInformationFormat?.players?.online ?: 0
            playerMaxNum = information.serverInformationFormat?.players?.max ?: info.playerMaxNum
        }.update()

        //发送log并获取服务器的关联群
        if (!((statusT == -1 && info.status != -1) || (statusT == 1 && info.status == -1)))
            return


        if (statusT == 1)
            log.info("服务器 ${info.name} 上线")
        else
            log.info("服务器 ${info.name} 离线")

        MinecraftServerMap { serverID = info.id }.findByServerId().forEach {
            groups.add(bot.getGroup(it.groupID) ?: return@forEach)
        }

        val data = info.msgMaker(statusT, players, groups[1])

        groups.forEach {
            it.sendMessage(data)
        }
    }

    private suspend fun MinecraftServer.msgMaker(status: Int, playerList: Players?, subject: Contact): CodableMessage {
        return if (status == 1)
            subject.uploadImage(
                MinecraftDataImgGenerator.getImg(
                    playerList!!.players,
                    "${playerList.online}/${playerList.max}",
                    this.host,
                    this.port.toString()
                )

            ).toMessageChain()
        else
            PlainText(
                """
                    :(
                    ${this.name} is Offline
                    IP: ${this.host}:${this.port}
                    """.trimIndent()
            )
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

}

