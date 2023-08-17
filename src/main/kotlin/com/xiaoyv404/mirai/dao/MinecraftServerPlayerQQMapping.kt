package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.database.Database
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayerQQMapping
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayerQQMappings
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayers
import com.xiaoyv404.mirai.model.mincraftServer.Permissions
import org.ktorm.dsl.*
import org.ktorm.entity.*

private val org.ktorm.database.Database.minecraftServerPlayerQQMapping
    get() = this.sequenceOf(
        MinecraftServerPlayerQQMappings
    )


fun MinecraftServerPlayerQQMapping.getPermissionByQQ(): Permissions? {
    return Database.db
        .from(MinecraftServerPlayerQQMappings)
        .innerJoin(
            MinecraftServerPlayers,
            on = MinecraftServerPlayerQQMappings.playerName eq MinecraftServerPlayers.name
        )
        .select(
            MinecraftServerPlayerQQMappings.qq,
            MinecraftServerPlayerQQMappings.lock,
            MinecraftServerPlayers.permissions
        )
        .where { MinecraftServerPlayerQQMappings.qq eq this.qq and MinecraftServerPlayerQQMappings.lock eq true }
        .map{
            it[MinecraftServerPlayers.permissions]
        }.firstOrNull()
}

/**
 * @return false 新增
 * @return true 更新
 */
fun MinecraftServerPlayerQQMapping.save(): Boolean {
    return if (this.findByQQId() == null) {
        Database.db.minecraftServerPlayerQQMapping.add(this)
        false
    } else {
        Database.db.minecraftServerPlayerQQMapping.update(this)
        true
    }
}

fun MinecraftServerPlayerQQMapping.findByQQId(): MinecraftServerPlayerQQMapping? {
    return Database.db.minecraftServerPlayerQQMapping.find { it.qq eq this.qq }
}

fun MinecraftServerPlayerQQMapping.findByPlayerName(): List<MinecraftServerPlayerQQMapping> {
    return Database.db.minecraftServerPlayerQQMapping.toList()
        .filter { it.playerName.lowercase() == this.playerName.lowercase() }
}