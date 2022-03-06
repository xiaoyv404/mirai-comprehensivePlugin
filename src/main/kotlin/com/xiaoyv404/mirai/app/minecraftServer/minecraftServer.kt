package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Command
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
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.buildForwardMessage
import java.util.*

fun minecraftServerEntrance() {
    Timer().schedule(object : TimerTask() {
        override fun run() {
            PluginMain.launch {
                getAll().forEach {
                    MinecraftServerStatusRequester().check(it)
                }
            }
        }
    }, Date(), 60000)
    GlobalEventChannel.subscribeGroupMessages {
        finding(Command.minecraftServerStats) {
            val rd = it.groups
            MinecraftServerMap {
                groupID = group.id
            }.findByGroupId().forEach { si ->
                val info = MinecraftServer {
                    id = si.serverID
                }.findById()
                if (info != null) {
                    MinecraftServerStatusRequester(group).check(
                        info,
                        if (rd[9] != null)
                            2U
                        else
                            1U
                    )
                }
            }
        }
    }
}


class MinecraftServerStatusRequester(private var group: Contact? = null) {
    suspend fun check(si: MinecraftServer, control: UInt = 0U) {
        PluginMain.launch {
            val dStatus = si.status
            if (dStatus != -2) {
                val information = getServerInfo(si.host, si.port)
                val players = information.serverInformationFormat?.players
                val groups = mutableListOf<Contact>()
                val statusD = information.status

                val statusT = if (statusD != 1)
                    if (dStatus == 1 && control == 0U)
                        0
                    else
                        -1
                else
                    1


                if ((statusT == -1 && dStatus != -1) || (statusT == 1 && dStatus == -1)) {
                    if (statusT == 1)
                        PluginMain.logger.info("服务器 ${si.name} 上线")
                    else
                        PluginMain.logger.info("服务器 ${si.name} 离线")
                    MinecraftServerMap { serverID = si.id }.findByServerId().forEach {
                        groups.add(Bot.getInstance(2079373402).getGroup(it.groupID) ?: return@forEach)
                    }
                } else
                    group?.let { groups.add(it) }

                if (statusT != dStatus) {
                    MinecraftServer {
                        id = si.id
                        status = statusT
                    }.update()
                }

                groups.forEach {
                    if (statusT == 1) {
                        players?.let { it1 -> it.sendImage(MinecraftDataImgGenerator.getImg(it1.players,"${players.online}/${players.max}",si.host,si.port.toString())) }
                    } else
                        it.sendMessage(
                            """
                                :(
                                ${si.name} is Offline
                                IP: ${si.host}:${si.port}
                            """.trimIndent()
                        )
                }

                if (control == 2U) {
                    if (players != null) {
                        if (players.online == 0){
                            group!!.sendMessage("都没有人为什么要播报玩家列表呢（恼）")
                            return@launch
                        }
                        sendPlayerList(getPlayerList(si.host,si.port,players))
                    }
                }
            }
        }
    }

    private suspend fun getPlayerList(host: String, port: Int, players: Players): List<Player> {
        val playersML = players.players.toMutableList()
        var cycles = players.online / 12
        if (cycles != 0)
            cycles++

        PluginMain.logger.info("尝试获取PlayList次数：${cycles+1}次")

        for (i in 1..cycles) {
            getServerInfo(host, port).serverInformationFormat?.players?.players?.forEach {
                playersML.add(it)
            }
        }

        val playersL = playersML.distinct()
        PluginMain.logger.info("已获取到玩家列表人数：${playersL.size}")
        return playersL
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


@OptIn(ExperimentalSerializationApi::class)
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
    } catch (e: Exception) {
        PluginMain.logger.debug(e.message)
        pJ.status = 0
        pJ
    }
}