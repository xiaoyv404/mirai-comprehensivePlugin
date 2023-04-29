package com.xiaoyv404.mirai.model.mincraftServer

import org.ktorm.entity.*
import org.ktorm.schema.*

interface MinecraftServerMap : Entity<MinecraftServerMap> {
    companion object : Entity.Factory<MinecraftServerMap>()
    var groupID: Long
    var serverID: Int
}


object MinecraftServerMaps : Table<MinecraftServerMap>("MinecraftServer_Map") {
    val groupID = long("groupID").primaryKey().bindTo { it.groupID }
    val serverID = int("serverID").primaryKey().bindTo { it.serverID }
}