package com.xiaoyv404.mirai.databace.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.long

interface User : Entity<User> {
    val id: Long
    val admin: Boolean
    val bot: Boolean
    val setu: Boolean
}

object Users : Table<User>("Users") {
    val id = long("id").primaryKey()
    val admin = boolean("admin")
    val bot = boolean("bot")
    val setu = boolean("setu")
}