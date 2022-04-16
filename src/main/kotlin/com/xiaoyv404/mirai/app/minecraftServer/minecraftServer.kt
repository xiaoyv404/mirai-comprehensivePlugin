package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.core.gid
import com.xiaoyv404.mirai.databace.dao.*
import com.xiaoyv404.mirai.tool.KtorUtils
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.toMessageChain
import org.apache.commons.cli.Options
import java.util.*

@ExperimentalSerializationApi
@App
class MinecraftServerStats : NfApp(), IFshApp {
    override fun getAppName() = "MinecraftServerStats"
    override fun getVersion() = "1.0.1"
    override fun getAppDescription() = "�ҵ����������״̬���"
    override fun getCommands() =
        arrayOf("-����������û", "-������״̬", "-��������û", "-����״̬", "-����ͷ����û", "-����ͷ״̬", "-ServerStatus", "-PotatoStatus")


    private val options = Options().apply {
        addOption("p", "player", false, "��ȡ����б�")
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
        val players = infoD.serverInformationFormat!!.players

        //�жϵ�ǰ������״̬
        val statusT = if (infoD.status != 1 && info.status == 1)
            -1
        else
            1

        //ͨ��״̬������ʾ��
        val msgA =
            if (statusT == 1)
                msg.subject.uploadImage(
                    players.let {
                        MinecraftDataImgGenerator.getImg(
                            it.players,
                            "${players.online}/${players.max}",
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

        //��ȡ����������Ⱥ����������ʾ
        if (statusT != info.status)
            MinecraftServerMap {
                serverID = info.id
            }.findByServerId().forEach {
                (bot.getGroup(it.groupID) ?: return@forEach).sendMessage(msgA)
            }
        else
            msg.reply(msgA, false)

        //����������ҷ��������ߣ���������б�
        if (statusT == 1 && playerList) {
            sendPlayerList(msg, getPlayerList(info.host, info.port, players))
        }
    }

    private suspend fun getPlayerList(host: String, port: Int, players: Players): List<Player> {
        val playersML = players.players.toMutableList()
        var cycles = players.online / 12
        if (cycles != 0)
            cycles++

        log.info("���Ի�ȡPlayList������${cycles + 1}��")

        for (i in 1..cycles) {
            getServerInfo(host, port).serverInformationFormat?.players?.players?.forEach {
                playersML.add(it)
            }
        }

        val playersL = playersML.distinct()
        log.info("�ѻ�ȡ������б�������${playersL.size}")
        return playersL
    }

    private suspend fun sendPlayerList(msg: MessageEvent, players: List<Player>) {
        msg.reply(
            buildForwardMessage(msg.subject) {
                players.forEach { player ->
                    msg.subject.bot.says(
                        """
                        name: ${player.name}
                        d: ${player.id}
                        """.trimIndent())
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

            //�жϷ�����������ʲô״̬
            val statusT = if (statusD != 1)
                if (info.status == 1)
                    0
                else
                    -1
            else
                1

            //����log����ȡ�������Ĺ���Ⱥ
            if ((statusT == -1 && info.status != -1) || (statusT == 1 && info.status == -1)) {
                if (statusT == 1)
                    log.info("������ ${info.name} ����")
                else
                    log.info("������ ${info.name} ����")
                MinecraftServerMap { serverID = info.id }.findByServerId().forEach {
                    groups.add(bot.getGroup(it.groupID) ?: return@forEach)
                }
            }

            //�������ݿ���״̬
            if (statusT != info.status) {
                MinecraftServer {
                    id = info.id
                    status = statusT
                }.update()
            }

            //����״̬��ʾ
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
                KtorUtils.normalClient.get(
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