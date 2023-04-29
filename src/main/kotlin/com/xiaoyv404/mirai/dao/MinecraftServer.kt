package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.databace.*
import com.xiaoyv404.mirai.model.mincraftServer.*
import org.ktorm.dsl.*
import org.ktorm.entity.*

private val org.ktorm.database.Database.minecraftServers get() = this.sequenceOf(MinecraftServers)

fun MinecraftServer.findById(): MinecraftServer? {
    return Database.db.minecraftServers.find { MinecraftServers.id eq this.id }
}

fun MinecraftServer.update(): Int {
    return Database.db.minecraftServers.update(this)
}

fun getAll(): List<MinecraftServer> {
    return Database.db.minecraftServers.toList()
}

fun String.findByName(): MinecraftServer?{
    return Database.db.minecraftServers.find { MinecraftServers.name eq this }
}

fun MinecraftServer.toList(): List<MinecraftServer>{
    return Database.db.minecraftServers.toList()
}
