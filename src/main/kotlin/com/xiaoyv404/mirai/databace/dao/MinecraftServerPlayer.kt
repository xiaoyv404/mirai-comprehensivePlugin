package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.app.minecraftServer.*
import com.xiaoyv404.mirai.databace.Database.db
import org.ktorm.database.*
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.*
import java.time.*


interface MinecraftServerPlayer : Entity<MinecraftServerPlayer> {

    companion object : Entity.Factory<MinecraftServerPlayer>()

    var id: String
    var name: String
    var lastLoginTime: LocalDateTime
    var lastLoginServer: String
}

private val Database.minecraftServerPlayer get() = this.sequenceOf(MinecraftServerPlayers)

fun MinecraftServerPlayer.findById(): MinecraftServerPlayer? {
    return db.minecraftServerPlayer.find { it.id eq this.id }
}

fun MinecraftServerPlayer.findByName(): MinecraftServerPlayer? {
    return db.minecraftServerPlayer.find { it.name eq this.name }
}

fun MinecraftServerPlayer.save(): Boolean {
    return if (this.findById() == null) {
        db.minecraftServerPlayer.add(this)
        false
    } else {
        this.update()
        true
    }
}

fun MinecraftServerPlayer.update() {
    db.minecraftServerPlayer.update(this)
}

fun List<Player>.save(sererName: String) {
    this.forEach {
        MinecraftServerPlayer {
            this.id = it.id
            this.name = it.name
            this.lastLoginTime = LocalDateTime.now()
            this.lastLoginServer = sererName
        }.save()
    }
}

fun MinecraftServer.getOnlinePlayers(): List<MinecraftServerPlayer> {
    val players = db.minecraftServerPlayer.filter { it.lastLoginServer eq this.name }.toMutableList()
    val localDateTime = LocalDateTime.now()
    players.removeIf {
        Duration.between(it.lastLoginTime, localDateTime).toMinutes() > 4
    }
    return players.toList()
}

object MinecraftServerPlayers : Table<MinecraftServerPlayer>("MinecraftServerPlayers") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val lastLoginTime = datetime("lastLoginTime").bindTo { it.lastLoginTime }
    val lastLoginServer = varchar("lastLoginServer").bindTo { it.lastLoginServer }
}