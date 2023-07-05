package com.xiaoyv404.mirai.model

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.long

interface User : Entity<User> {
    companion object : Entity.Factory<User>()
    var id: Long
    val admin: Boolean
    var bot: Boolean
    var warningTimes: Int
}

object Users : Table<User>("Users") {
    val id = long("id").primaryKey().bindTo { it.id }
    val admin = boolean("admin").bindTo { it.admin }
    val bot = boolean("bot").bindTo { it.bot }
    val warningTimes = int("warning_times").bindTo { it.warningTimes }
}