package com.xiaoyv404.mirai.service.minecraftServer

import kotlinx.serialization.Serializable

data class ServerInformation(
    val id: Int,
    val host: String,
    val port: Int,
    val status: Int,
    val name: String
)

@Serializable
data class ServerInformationFormat(
    val description: String,
    val host: String,
    val icon: String,
    val motd: Motd,
    val players: Players,
    val port: Int,
    val protocolVersion: Int,
    val version: String
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