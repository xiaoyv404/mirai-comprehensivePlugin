package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.databace.dao.*
import com.xiaoyv404.mirai.databace.dao.mincraftServer.*
import com.xiaoyv404.mirai.databace.dao.mincraftServer.Permissions
import com.xiaoyv404.mirai.tool.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.*
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
            "-UpdatePermission"
        )


    private val options = Options().apply {
        addOption("p", "player", false, "获取玩家列表")
        addOption("s", "server", true, "选择服务器")
    }

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        val cmdLine = IFshApp.cmdLine(options, args)

        if (args[0] == "-UpdatePermission" && authorityIdentification(msg.uid(),msg.gid(),"MinecraftServerPlayerPermission")) {
            updatePermission(msg,args.getOrNull(1)?:return false)
            return true
        }

        val info = if (cmdLine.hasOption("server"))
            cmdLine.getOptionValue("server").findByName()
        else
            "MCG".findByName()

        if (info != null) {
            sendInfo(
                msg, info,
                cmdLine.hasOption("player")
            )
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
        val data = info.msgMaker(statusT, players, msg.subject)


        //获取服务器关联群，并发送提示
        if (statusT != info.status) {
            MinecraftServerMap {
                serverID = info.id
            }.findByServerId().forEach {
                (bot.getGroup(it.groupID) ?: return@forEach).sendMessage(data)
            }
            MinecraftServer {
                id = info.id
                status = statusT
            }.update()
        } else
            msg.reply(data, false)

        //如果有需求并且服务器在线，发送玩家列表
        //并更新在线玩家列表
        if (statusT == 1 && playerList) {
            getPlayerList(info.host, info.port, players!!).let {
                it.save(info.name)
                info.getOnlinePlayers().send(msg)
            }
        } else
            players?.players?.save(info.name)
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

            //更新在线玩家列表
            players?.players?.save(info.name)

            //发送log并获取服务器的关联群
            if ((statusT == -1 && info.status != -1) || (statusT == 1 && info.status == -1)) {
                if (statusT == 1)
                    log.info("服务器 ${info.name} 上线")
                else
                    log.info("服务器 ${info.name} 离线")
                MinecraftServerMap { serverID = info.id }.findByServerId().forEach {
                    groups.add(bot.getGroup(it.groupID) ?: return@forEach)
                }
                //更新数据库内状态
                MinecraftServer {
                    id = info.id
                    status = statusT
                }.update()
            } else {
                return@launch
            }

            val data = info.msgMaker(statusT, players, groups[1])


            groups.forEach {
                it.sendMessage(data)
            }
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

    private suspend fun List<MinecraftServerPlayer>.send(msg: MessageEvent) {
        if (this.isEmpty())
            msg.reply("都没有玩家怎么播报列表啊（恼）", quote = true)
        else
            msg.reply(
                buildForwardMessage(msg.subject) {
                    this@send.forEach { player ->
                        msg.subject.bot.says(
                            """
                        name: ${player.name}
                        id: ${player.id}
                        身份: ${player.permissions?.getPermissionByCode()?.permissionName ?: "毛玉"}
                        """.trimIndent()
                        )
                    }
                }.toMessageChain(), quote = false
            )
    }

    private suspend fun updatePermission(msg: MessageEvent, permissionName: String) {
        val players = Regex(".+").findAll(msg.nextMessage().contentToString())
        val notfoundPlayers = mutableListOf<String>()
        players.forEach {
            val effects = MinecraftServerPlayer {
                this.name = it.value
                this.permissions = Permissions.valueOf(permissionName).code
            }.update()
            if (effects == 0)
                notfoundPlayers.add(it.value)
        }
        if (notfoundPlayers.isEmpty())
            msg.reply("更新完成")
        else {
            msg.reply(
                "未找到: ${notfoundPlayers.joinToString(", ")}"
            )
        }
    }
}

