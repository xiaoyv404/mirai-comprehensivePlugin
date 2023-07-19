package com.xiaoyv404.mirai.model.mincraftServer

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

interface MinecraftServerPlayerQQMapping:Entity<MinecraftServerPlayerQQMapping> {
    companion object : Entity.Factory<MinecraftServerPlayerQQMapping>()

    var playerName: String
    var qq: Long
}

object MinecraftServerPlayerQQMappings : Table<MinecraftServerPlayerQQMapping>("MinecraftServerPlayer_qq_mapping") {
    val playerName = varchar("playerName").primaryKey().bindTo { it.playerName }
    val qq = long("qq").primaryKey().bindTo { it.qq }
}