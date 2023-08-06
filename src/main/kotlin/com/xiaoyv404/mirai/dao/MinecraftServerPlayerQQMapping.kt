package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayerQQMapping
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayerQQMappings
import org.ktorm.dsl.eq
import org.ktorm.entity.*

private val org.ktorm.database.Database.minecraftServerPlayerQQMapping
    get() = this.sequenceOf(
        MinecraftServerPlayerQQMappings
    )


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
    return Database.db.minecraftServerPlayerQQMapping.toList().filter { it.playerName.lowercase() == this.playerName.lowercase() }
}