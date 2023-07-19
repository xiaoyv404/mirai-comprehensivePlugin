package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayerQQMapping
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayerQQMappings
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

private val org.ktorm.database.Database.minecraftServerPlayerQQMapping
    get() = this.sequenceOf(
        MinecraftServerPlayerQQMappings
    )


/**
 * @return false 新增
 * @return true 更新
 */
fun MinecraftServerPlayerQQMapping.save(): MinecraftServerPlayerQQMapping? {
    val data = this.findByPlayerName()
    return if (data == null) {
        Database.db.minecraftServerPlayerQQMapping.add(this)
        null
    } else data
}

fun MinecraftServerPlayerQQMapping.findByPlayerName(): MinecraftServerPlayerQQMapping? {
    return Database.db.minecraftServerPlayerQQMapping.find { it.playerName eq this.playerName }
}