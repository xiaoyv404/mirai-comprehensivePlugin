package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.databace.Database.db
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

private val Database.minecraftServers get() = this.sequenceOf(MinecraftServers)

interface MinecraftServer : Entity<MinecraftServer> {
    companion object : Entity.Factory<MinecraftServer>()
    var id: Int
    val host: String
    val port: Int
    val status: Int
    val name: String
}

fun MinecraftServer.findById(): MinecraftServer? {
    return db.minecraftServers.find { it.id eq this.id }
}

fun MinecraftServer.update(){
    db.minecraftServers.update(this)
}

fun getAll(): List<MinecraftServer> {
    return db.minecraftServers.toList()
}


object MinecraftServers : Table<MinecraftServer>("MinecraftServer") {
    val id = int("id").primaryKey().bindTo { it.id }
    val host = varchar("host").bindTo { it.host }
    val port = int("port").bindTo { it.port }
    val status = int("status").bindTo { it.status }
    val name = varchar("name").bindTo { it.name }
}