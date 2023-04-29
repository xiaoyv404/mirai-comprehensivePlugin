package com.xiaoyv404.mirai.model.mincraftServer

import com.xiaoyv404.mirai.databace.*
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
    var permissions: Long?
    fun getAllOnlinePlayers(): List<MinecraftServerPlayer> {
        val players =
            Database.db.minecraftServerPlayer.filter {
                MinecraftServerPlayers.lastLoginTime between (LocalDateTime.now()
                    .plusMinutes(-4))..(LocalDateTime.now())
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
    val permissions = long("permissions").bindTo { it.permissions }
}

enum class Permissions(val code: Long, val permissionName: String) {
    Submit(0, "服主"),
    OP(1, "妖怪贤者"),
    WNEditor(2, "大妖怪"),
    WorldEditor(3, "工业妖怪"),
    NPCEditor(4, "读心妖怪"),
    Basic(5, "妖怪"),
}

fun Long.getPermissionByCode(): Permissions {
    return Permissions.values()[this.toInt()]
}

