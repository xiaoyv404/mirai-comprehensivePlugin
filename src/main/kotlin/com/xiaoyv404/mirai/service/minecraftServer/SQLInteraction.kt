package com.xiaoyv404.mirai.service.minecraftServer

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.MinecraftServerMaps
import com.xiaoyv404.mirai.databace.dao.MinecraftServers
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.netty.channel.ConnectTimeoutException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.Bot
import org.ktorm.dsl.*

val format = Json { ignoreUnknownKeys = true }

suspend fun serverStatusProcess() {
    val sIL = getServerInformation()
    sIL.forEach {
        if (it.status != -1) {
            try {
                val pJ = format.decodeFromString<ServerInformationFormat>(minecraftServerStatusCheck(it.host, it.port))
                if (it.status == 0) {
                    updateServerInformation(it.id, 1)
                    getServerMapByServerID(it.id).forEach { groupId ->
                        Bot.getInstance(2079373402).getGroupOrFail(groupId!!).sendMessage(
                            "服务器${it.name} is Online\n" +
                                "IP: ${it.host}:${it.port}\n" +
                                "人数: ${pJ.players.online}/${pJ.players.max}"
                        )
                    }
                }
            } catch (e: ServerResponseException) {
                when (it.status) {
                    1  -> updateServerInformation(it.id, -2)
                    -2 -> {
                        updateServerInformation(it.id, 0)
                        getServerMapByServerID(it.id).forEach { groupId ->
                            Bot.getInstance(2079373402).getGroup(groupId!!)!!.sendMessage(
                                ":(\n" +
                                    "${it.name} is Offline\n" +
                                    "IP: ${it.host}:${it.port}"
                            )
                        }
                    }
                }
            } catch (e: ConnectTimeoutException) {
                println("无法连接到分析服务器服务器")
            }
        }
    }
}

fun getServerInformation(): List<ServerInformation> {
    return Database.db
        .from(MinecraftServers)
        .select()
        .map { row ->
            ServerInformation(
                row[MinecraftServers.id]!!,
                row[MinecraftServers.host]!!,
                row[MinecraftServers.port]!!,
                row[MinecraftServers.status]!!,
                row[MinecraftServers.name]!!
            )
        }
}

fun getServerInformationByServerID(serverID: Int): List<ServerInformation> {
    return Database.db
        .from(MinecraftServers)
        .select()
        .where { MinecraftServers.id eq serverID }
        .map { row ->
            ServerInformation(
                row[MinecraftServers.id]!!,
                row[MinecraftServers.host]!!,
                row[MinecraftServers.port]!!,
                row[MinecraftServers.status]!!,
                row[MinecraftServers.name]!!
            )
        }
}

fun updateServerInformation(id: Int, status: Int) {
    Database.db
        .update(MinecraftServers) {
            set(it.status, status)
            where {
                it.id eq id
            }
        }
}

fun getServerMapByServerID(serverID: Int): List<Long?> {
    return Database.db
        .from(MinecraftServerMaps)
        .select()
        .where { MinecraftServerMaps.serverID eq serverID }
        .map { row ->
            row[MinecraftServerMaps.groupID]
        }
}

fun getServerMapByGroupID(groupID: Long): List<Int?> {
    return Database.db
        .from(MinecraftServerMaps)
        .select()
        .where { MinecraftServerMaps.groupID eq groupID }
        .map { row ->
            row[MinecraftServerMaps.serverID]
        }
}

suspend fun minecraftServerStatusCheck(host: String, port: Int): String {
    return HttpClient().use { clien -> clien.get("http://127.0.0.1:8080/server?host=$host&port=$port") }
}