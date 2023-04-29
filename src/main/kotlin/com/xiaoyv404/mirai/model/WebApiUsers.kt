package com.xiaoyv404.mirai.model

import org.ktorm.entity.*
import org.ktorm.schema.*

interface WebApiUser : Entity<WebApiUser> {
    companion object : Entity.Factory<WebApiUser>()

    val id: Long
    var name: String
    var password: String
    var qid: Long
    val authority: Int
}

object WebApiUsers : Table<WebApiUser>("WebApiUsers") {
    val id = long("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val password = text("password").bindTo { it.password }
    val qid = long("qid").bindTo { it.qid }
    val authority = int("authority").bindTo { it.authority }
}