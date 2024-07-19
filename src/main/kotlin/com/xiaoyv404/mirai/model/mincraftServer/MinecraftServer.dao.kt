package com.xiaoyv404.mirai.model.mincraftServer

import org.ktorm.entity.Entity
import org.ktorm.schema.*


interface MinecraftServer : Entity<MinecraftServer> {
    companion object : Entity.Factory<MinecraftServer>()
    var id: Int
    val host: String
    val port: Int
    var status: MinecraftServerStatus
    val name: String
    var playerNum: Int
    var playerMaxNum: Int
    val mock: Boolean
    val hilde: Boolean
}

object MinecraftServers : Table<MinecraftServer>("MinecraftServer") {
    val id = int("id").primaryKey().bindTo { it.id }
    val host = varchar("host").bindTo { it.host }
    val port = int("port").bindTo { it.port }
    val status = enum<MinecraftServerStatus>("status").bindTo { it.status }
    val name = varchar("name").bindTo { it.name }
    val playerNum  = int("playerNum").bindTo { it.playerNum }
    val playerMaxNum = int("playerMaxNum").bindTo { it.playerMaxNum }
    val mock = boolean("mock").bindTo { it.mock }
    val hilde = boolean("hilde").bindTo { it.hilde }
}

enum class MinecraftServerStatus {
    Online,
    Offline,
    Uncertain,
}