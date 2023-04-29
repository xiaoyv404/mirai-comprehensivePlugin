package com.xiaoyv404.mirai.model

import org.ktorm.entity.*
import org.ktorm.schema.*

interface User : Entity<User> {
    companion object : Entity.Factory<User>()
    var id: Long
    val admin: Boolean
    var bot: Boolean
}

object Users : Table<User>("Users") {
    val id = long("id").primaryKey().bindTo { it.id }
    val admin = boolean("admin").bindTo { it.admin }
    val bot = boolean("bot").bindTo { it.bot }
}