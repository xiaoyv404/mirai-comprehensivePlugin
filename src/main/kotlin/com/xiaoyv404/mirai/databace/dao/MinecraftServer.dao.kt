package com.xiaoyv404.mirai.databace.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface MinecraftServer : Entity<MinecraftServer> {
    val id: Int
    val host: String
    val port: Int
    val status: Int
    val name: String
}

object MinecraftServers : Table<MinecraftServer>("MinecraftServer") {
    val id = int("id").primaryKey()
    val host = varchar("host").primaryKey()
    val port = int("port").primaryKey()
    val status = int("status").primaryKey()
    val name = varchar("name")
}