package com.xiaoyv404.mirai.databace.dao.mincraftServer

import com.xiaoyv404.mirai.app.minecraftServer.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.databace.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
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

private val org.ktorm.database.Database.minecraftServerPlayer get() = this.sequenceOf(MinecraftServerPlayers)

fun MinecraftServerPlayer.findById(): MinecraftServerPlayer? {
    return Database.db.minecraftServerPlayer.find { MinecraftServerPlayers.id eq this.id }
}

fun MinecraftServerPlayer.findByName(): MinecraftServerPlayer? {
    return Database.db.minecraftServerPlayer.find { MinecraftServerPlayers.name eq this.name }
}

fun MinecraftServerPlayer.save(): Boolean {
    return if (this.findById() == null) {
        Database.db.minecraftServerPlayer.add(this)
        false
    } else {
        this.update()
        true
    }
}

fun MinecraftServerPlayer.update() {
    Database.db.minecraftServerPlayer.update(this)
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
    val players =
        Database.db.minecraftServerPlayer.filter {
            MinecraftServerPlayers.lastLoginTime between (LocalDateTime.now()
                .plusMinutes(-4))..(LocalDateTime.now()) and (MinecraftServerPlayers.lastLoginServer eq this.name)
        }.toList()
    return players
}

suspend fun List<MinecraftServerPlayer>.send(msg: MessageEvent) {
    if (this.isEmpty()) {
        msg.reply("都没有玩家怎么播报列表啊（恼）", quote = true)
        return
    }

    msg.reply(
        buildForwardMessage(msg.subject) {
            this@send.forEach { player ->
                msg.subject.bot.says(
                    """
                        名字: ${player.name}
                        ${
                        if (Duration.between(player.lastLoginTime, LocalDateTime.now())
                                .toMinutes() > 4
                        ) "不在线" else "在线"
                    }
                        最后在线时间: ${player.lastLoginTime}
                        服务器: ${player.lastLoginServer}
                        UUID: ${player.id}
                        身份: ${player.permissions?.getPermissionByCode()?.permissionName ?: "毛玉"}
                    """.trimIndent()
                )
            }
        }.toMessageChain(), quote = false
    )
}