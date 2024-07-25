package com.xiaoyv404.mirai.model.mincraftServer

import com.xiaoyv404.mirai.core.NfClock
import com.xiaoyv404.mirai.database.Database
import org.ktorm.dsl.between
import org.ktorm.entity.Entity
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.enum
import org.ktorm.schema.varchar
import java.time.LocalDateTime


interface MinecraftServerPlayer : Entity<MinecraftServerPlayer> {
    companion object : Entity.Factory<MinecraftServerPlayer>()

    var id: String
    var name: String
    var lastLoginTime: LocalDateTime
    var lastLoginServer: String
    var permissions: Permissions
    fun getAllOnlinePlayers(): List<MinecraftServerPlayer> {
        val now = NfClock.now()
        val players =
            Database.db.minecraftServerPlayer.filter {
                MinecraftServerPlayers.lastLoginTime between (now
                    .plusMinutes(-4))..(now)
            }.toList()
        return players
    }

    private val org.ktorm.database.Database.minecraftServerPlayer get() = this.sequenceOf(MinecraftServerPlayers)
}

object MinecraftServerPlayers : Table<MinecraftServerPlayer>("MinecraftServerPlayers") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val lastLoginTime = datetime("lastLoginTime").bindTo { it.lastLoginTime }
    val lastLoginServer = varchar("lastLoginServer").bindTo { it.lastLoginServer }
    val permissions = enum<Permissions>("permissions").bindTo { it.permissions }
}

enum class Permissions(val permissionName: String) {
    Default("毛玉"),
    Basic("妖怪"),
    NPCEditor("读心妖怪"),
    WorldEditor("工业妖怪"),
    WNEditor("大妖怪"),
    OP("妖怪贤者"),
}