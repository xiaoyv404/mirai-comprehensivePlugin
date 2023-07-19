package com.xiaoyv404.mirai.model.mincraftServer

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

interface MinecraftServerPlayerQQMapping:Entity<MinecraftServerPlayerQQMapping> {
    companion object : Entity.Factory<MinecraftServerPlayerQQMapping>()

    var qq: Long
    var playerName: String
}

object MinecraftServerPlayerQQMappings : Table<MinecraftServerPlayerQQMapping>("MinecraftServerPlayer_qq_mapping") {
    val qq = long("qq").primaryKey().primaryKey().bindTo { it.qq }
    val playerName = varchar("playerName").bindTo { it.playerName }
}