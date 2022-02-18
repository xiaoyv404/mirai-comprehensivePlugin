package com.xiaoyv404.mirai.service.minecraftServer

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.MinecraftServerMaps
import com.xiaoyv404.mirai.databace.dao.MinecraftServers
import org.ktorm.dsl.*


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