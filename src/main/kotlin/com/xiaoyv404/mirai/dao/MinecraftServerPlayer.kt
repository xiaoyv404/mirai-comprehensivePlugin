package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.app.minecraftServer.Player
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServer
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayer
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayers
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.toMessageChain
import org.ktorm.dsl.and
import org.ktorm.dsl.between
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import java.time.LocalDateTime

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
                        服务器: ${player.lastLoginServer}
                        UUID: ${player.id}
                        身份: ${player.permissions.permissionName}
                    """.trimIndent()
                )
            }
        }.toMessageChain(), quote = false
    )
}

private val org.ktorm.database.Database.minecraftServerPlayer get() = this.sequenceOf(MinecraftServerPlayers)

fun MinecraftServerPlayer.findById(): MinecraftServerPlayer? {
    return Database.db.minecraftServerPlayer.find { MinecraftServerPlayers.id eq this.id }
}

fun MinecraftServerPlayer.findByNameAndServer(): MinecraftServerPlayer? {
    return Database.db.minecraftServerPlayer.find { MinecraftServerPlayers.name eq this.name and (MinecraftServerPlayers.lastLoginServer eq this.lastLoginServer) }
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

