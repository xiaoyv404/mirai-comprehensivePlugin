package com.xiaoyv404.mirai.app.webAPI.model

import com.xiaoyv404.mirai.model.mincraftServer.Permissions
import kotlinx.serialization.Serializable

@Serializable
data class MinecraftPlayerApiModel(
    val id: String,
    val name: String,
    val lastLoginTime: String,
    val lastLoginServer: String,
    val permissions: Permissions,
)