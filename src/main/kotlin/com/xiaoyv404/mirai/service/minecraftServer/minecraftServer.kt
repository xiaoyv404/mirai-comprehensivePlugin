package com.xiaoyv404.mirai.service.minecraftServer

import com.xiaoyv404.mirai.databace.Command
import io.ktor.client.features.*
import io.netty.channel.ConnectTimeoutException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.buildForwardMessage
import java.util.*

@DelicateCoroutinesApi
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
                        try {
                            val pJ =
                                format.decodeFromString<ServerInformationFormat>(
                                    minecraftServerStatusCheck(
                                        sv.host,
                                        sv.port
                                    )
                                )
                            group.sendMessage(
                                "服务器${sv.name} is Online\n" +
                                    "IP: ${sv.host}:${sv.port}\n" +
                                    "人数: ${pJ.players.online}/${pJ.players.max}"
                            )// todo 建议回答已经被吃掉了（离线）/熟了（极卡）/快熟了（有点卡）/ 还没熟（不咋卡）
                            if (rd[10]?.value == "-p") {
                                group.sendMessage(
                                    buildForwardMessage {
                                        pJ.players.players.forEach { player ->
                                            bot.says(
                                                "name: ${player.name}\n" +
                                                    "id: ${player.id}"
                                            )
                                        }
                                    }
                                )
                            }
                        } catch (e: ServerResponseException) {
                            group.sendMessage(
                                ":(\n" +
                                    "${sv.name} is Offline\n" +
                                    "IP: ${sv.host}:${sv.port}"
                            )
                        } catch (e: ConnectTimeoutException) {
                            group.sendMessage("无法连接到分析服务器")
                        }
                    }
                }
            }
        }
    }
}