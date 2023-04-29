package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.databace.*
import com.xiaoyv404.mirai.model.mincraftServer.*
import org.ktorm.dsl.*
import org.ktorm.entity.*

private val org.ktorm.database.Database.minecraftServerMap get() = this.sequenceOf(MinecraftServerMaps)

fun MinecraftServerMap.findByServerId(): List<MinecraftServerMap> {
    return Database.db.minecraftServerMap.filter { MinecraftServerMaps.serverID eq this.serverID }.toList()
}

fun MinecraftServerMap.findByGroupId(): List<MinecraftServerMap> {
    return Database.db.minecraftServerMap.filter { MinecraftServerMaps.groupID eq this.groupID }.toList()
}
