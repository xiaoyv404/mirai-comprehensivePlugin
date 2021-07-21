package com.xiaoyv404.mirai.databace.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long

interface MinecraftServerMap : Entity<MinecraftServerMap> {
    val groupID: Long
    val serverID: Int
}

object MinecraftServerMaps : Table<MinecraftServerMap>("MinecraftServer_Map") {
    val groupID = long("groupID").primaryKey()
    val serverID = int("serverID").primaryKey()
}