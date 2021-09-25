package com.xiaoyv404.mirai.service.minecraftServer

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.service.tool.KtorUtils
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.buildForwardMessage
import java.util.*

@KtorExperimentalAPI
fun minecraftServerEntrance() {
    Timer().schedule(object : TimerTask() {
        override fun run() {
            PluginMain.launch {
                getServerInformation().forEach {
                    MinecraftServerStatusRequester().check(it)
                }
            }
        }
    }, Date(), 60000)
    GlobalEventChannel.subscribeGroupMessages {
        finding(Command.minecraftServerStats) {
            val rd = it.groups
            if (rd[10]?.value == "-h" || rd[10]?.value == "--help") {
                group.sendMessage(
                    "这是正则:\n" +
                        "   ^(404 (((服务器|土豆|破推头)(熟了没|状态))|((?i)((Server|Potato)Stats)))( ((-h|--help)|(-p)))?)\$\n" +
                        "诶?你问我为什么不写之前的那种命令帮助了?唔。。。你自己看嘛,又不是不能看"
                )
            } else {
                getServerMapByGroupID(group.id).forEach { si ->
                    getServerInformationByServerID(si!!).forEach { sv ->
                        MinecraftServerStatusRequester(group).check(
                            sv,
                            if (rd[10]?.value == "-p")
                                2U
                            else
                                1U
                        )
                    }
                }
            }
        }
    }
}

class MinecraftServerStatusRequester(private var group: Contact? = null) {
    @KtorExperimentalAPI
    suspend fun check(si: ServerInformation, control: UInt = 0U) {
        PluginMain.launch {
            val dStatus = si.status

            if (dStatus != -2) {
                try {
                    val information = getServerInfo(si.host, si.port)
                    val players = information.serverInformationFormat?.players
                    val groups = mutableListOf<Contact>()
                    val status = information.status

                    if (dStatus != status.toInt() && dStatus != 1 && !((dStatus == -1) && (status == 1U))) {
                        getServerMapByServerID(si.id).forEach { gid ->
                            groups.add(Bot.getInstance(2079373402).getGroup(gid!!)!!)
                        }
                    } else {
                        group?.let { groups.add(it) }
                    }
                    when (status) {
                        1U -> {
                            groups.forEach { g ->
                                g.sendMessage(
                                    "服务器${si.name} is Online\n" +
                                        "IP: ${si.host}:${si.port}\n" +
                                        "人数: ${players!!.online}/${players.max}"
                                )// todo 建议回答已经被吃掉了（离线）/熟了（极卡）/快熟了（有点卡）/ 还没熟（不咋卡）
                            }

                            if (control == 2U) {
                                sendPlayerList(players!!.players)
                            }
                            if (dStatus != 1) {
                                updateServerInformation(si.id, 1)
                            }
                        }
                        0U -> {
                            groups.forEach { g ->
                                g.sendMessage(
                                    ":(\n" +
                                        "${si.name} is Offline\n" +
                                        "IP: ${si.host}:${si.port}"
                                )
                            }
                            when (dStatus) {
                                1 -> updateServerInformation(
                                    si.id,
                                    if (control == 0U)
                                        -1
                                    else
                                        0
                                )
                                -1 -> updateServerInformation(si.id, 0)
                            }
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    group?.sendMessage("无法连接到分析服务器")
                }
            }
        }
    }

    private suspend fun sendPlayerList(players: List<Player>) {
        group!!.sendMessage(
            buildForwardMessage(group!!) {
                players.forEach { player ->
                    group!!.bot.says(
                        "name: ${player.name}\n" +
                            "id: ${player.id}"
                    )
                }
            }
        )
    }
}


@KtorExperimentalAPI
suspend fun getServerInfo(host: String, port: Int): ServerInformationFormatAndStatus {
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
    } catch (e: ServerResponseException) {
        pJ.status = 0U
        pJ
    }
}