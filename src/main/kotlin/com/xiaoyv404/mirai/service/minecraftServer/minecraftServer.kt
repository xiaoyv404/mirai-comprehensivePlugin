package com.xiaoyv404.mirai.service.minecraftServer

import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.service.tool.KtorUtils
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.util.*
import io.netty.channel.ConnectTimeoutException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.buildForwardMessage
import java.util.*

@KtorExperimentalAPI
fun minecraftServerEntrance() {
    Timer().schedule(object : TimerTask() {
        override fun run() {
            GlobalScope.launch {
                serverStatusProcess()
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
                        MinecraftServerStatusRequester(listOf(group)).check(
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

class MinecraftServerStatusRequester(private val group: List<Contact>) {
    @KtorExperimentalAPI
    suspend fun check(si: ServerInformation, control: UInt? = 0U) {
        if (si.status != -1) {
            try {
                val pJ = getServerInfo(si.host, si.port)
                updateServerInformation(si.id, 1)
                if (si.status == 0 || control != 0U) {
                    group.forEach { g ->
                        g.sendMessage(
                            "服务器${si.name} is Online\n" +
                                "IP: ${si.host}:${si.port}\n" +
                                "人数: ${pJ.players.online}/${pJ.players.max}"

                        )// todo 建议回答已经被吃掉了（离线）/熟了（极卡）/快熟了（有点卡）/ 还没熟（不咋卡）
                    }
                    if (control == 2U) {
                        group[0].let {
                            it.sendMessage(
                                buildForwardMessage(it) {
                                    pJ.players.players.forEach { player ->
                                        it.bot.says(
                                            "name: ${player.name}\n" +
                                                "id: ${player.id}"
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            } catch (e: ServerResponseException) {
                when {
                    (si.status == 1 && control == 0U) ->
                        updateServerInformation(si.id, -2)
                    (si.status == -1 || control != 0U) -> {
                        updateServerInformation(si.id, 0)
                        group.forEach { g ->
                            g.sendMessage(
                                ":(\n" +
                                    "${si.name} is Offline\n" +
                                    "IP: ${si.host}:${si.port}"
                            )
                        }
                    }

                }
            } catch (e: ConnectTimeoutException) {
                group.forEach { g ->
                    g.sendMessage("无法连接到分析服务器")
                }
            }
        }
    }
}

@KtorExperimentalAPI
suspend fun getServerInfo(host: String, port: Int): ServerInformationFormat {
    return Json.decodeFromString<ServerInformationFormat>(
        KtorUtils.normalClient.get(
            "http://127.0.0.1:8080/server?" +
                "host=$host&" +
                "port=$port"
        )
    )
}