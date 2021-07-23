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
                    "��������:\n" +
                        "   ^(404 (((������|����|����ͷ)(����û|״̬))|((?i)((Server|Potato)Stats)))( ((-h|--help)|(-p)))?)\$\n" +
                        "��?������Ϊʲô��д֮ǰ���������������?���������Լ�����,�ֲ��ǲ��ܿ�"
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
                                "������${sv.name} is Online\n" +
                                    "IP: ${sv.host}:${sv.port}\n" +
                                    "����: ${pJ.players.online}/${pJ.players.max}"
                            )// todo ����ش��Ѿ����Ե��ˣ����ߣ�/���ˣ�������/�����ˣ��е㿨��/ ��û�죨��զ����
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
                            group.sendMessage("�޷����ӵ�����������")
                        }
                    }
                }
            }
        }
    }
}