package com.xiaoyv404.mirai.entity.mincraftServer

import org.ktorm.entity.*
import org.ktorm.schema.*


interface MinecraftServer : Entity<MinecraftServer> {
    companion object : Entity.Factory<MinecraftServer>()
    var id: Int
    val host: String
    val port: Int
    var status: Int
    val name: String
    var playerNum: Int
    var playerMaxNum: Int
}



object MinecraftServers : Table<MinecraftServer>("MinecraftServer") {
    val id = int("id").primaryKey().bindTo { it.id }
    val host = varchar("host").bindTo { it.host }
    val port = int("port").bindTo { it.port }
    val status = int("status").bindTo { it.status }
    val name = varchar("name").bindTo { it.name }
    val playerNum  = int("playerNum").bindTo { it.playerNum }
    val playerMaxNum = int("playerMaxNum").bindTo { it.playerMaxNum }
}