package com.xiaoyv404.mirai.databace.dao.mincraftServer

import com.xiaoyv404.mirai.databace.Database.db
import org.ktorm.database.*
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.*


interface MinecraftServer : Entity<MinecraftServer> {
    companion object : Entity.Factory<MinecraftServer>()
    var id: Int
    val host: String
    val port: Int
    var status: Int
    val name: String
}

private val Database.minecraftServers get() = this.sequenceOf(MinecraftServers)

fun MinecraftServer.findById(): MinecraftServer? {
    return db.minecraftServers.find { MinecraftServers.id eq this.id }
}

fun MinecraftServer.update(): Int {
    return db.minecraftServers.update(this)
}

fun getAll(): List<MinecraftServer> {
    return db.minecraftServers.toList()
}

fun String.findByName(): MinecraftServer?{
    return db.minecraftServers.find { MinecraftServers.name eq this }
}


object MinecraftServers : Table<MinecraftServer>("MinecraftServer") {
    val id = int("id").primaryKey().bindTo { it.id }
    val host = varchar("host").bindTo { it.host }
    val port = int("port").bindTo { it.port }
    val status = int("status").bindTo { it.status }
    val name = varchar("name").bindTo { it.name }
}