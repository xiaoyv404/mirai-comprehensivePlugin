package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.databace.Database.db
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.Entity
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long

interface MinecraftServerMap : Entity<MinecraftServerMap> {
    companion object : Entity.Factory<MinecraftServerMap>()
    var groupID: Long
    var serverID: Int
}

private val Database.minecraftServerMap get() = this.sequenceOf(MinecraftServerMaps)

fun MinecraftServerMap.findByServerId(): List<MinecraftServerMap> {
    return db.minecraftServerMap.filter { it.serverID eq this.serverID }.toList()
}

fun MinecraftServerMap.findByGroupId(): List<MinecraftServerMap> {
    return db.minecraftServerMap.filter { it.groupID eq this.groupID }.toList()
}

object MinecraftServerMaps : Table<MinecraftServerMap>("MinecraftServer_Map") {
    val groupID = long("groupID").primaryKey().bindTo { it.groupID }
    val serverID = int("serverID").primaryKey().bindTo { it.serverID }
}