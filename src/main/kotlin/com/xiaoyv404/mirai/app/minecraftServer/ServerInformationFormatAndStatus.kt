package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerStatus
import kotlinx.serialization.Serializable

data class ServerInformationFormatAndStatus(
    var serverInformationFormat: ServerInformationFormat? = null,
    var status: MinecraftServerStatus = MinecraftServerStatus.Online
)

@Serializable
data class ServerInformationFormat(
    val description: String,
    val host: String,
    val icon: String? = null,
    val motd: Motd,
    val players: Players,
    val port: Int,
    val protocolVersion: Int,
    val version: String,
)

@Serializable
data class Motd(
    val html: String,
    val raw: String,
    val stripped: String
)

@Serializable
data class Players(
    val max: Int,
    val online: Int,
    val players: List<Player>
)

@Serializable
data class Player(
    val id: String,
    val name: String
)