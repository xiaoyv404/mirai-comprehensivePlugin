package com.xiaoyv404.mirai.app.webAPI.model

import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServer
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerStatus
import kotlinx.serialization.Serializable

@Serializable
class MinecraftServerApiModel(
    val name: String,
    val host: String,
    val port: Int,
    val status: Int,
    val playerNum: Int,
    val playerMaxNum: Int
)

fun MinecraftServer.apiModel(): MinecraftServerApiModel {
    return MinecraftServerApiModel(
        this.name,
        this.host,
        this.port,
        when (this.status) {
            MinecraftServerStatus.Online -> 1
            MinecraftServerStatus.Offline -> -1
            MinecraftServerStatus.Uncertain -> 1
        },
        this.playerNum,
        this.playerMaxNum
    )
}