package com.xiaoyv404.mirai.databace.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.*

interface WebApiUser : Entity<WebApiUser> {
    val id: Long
    val name: String
    val password: String
    val qid: Long
    val authority: Int
}

object WebApiUsers : Table<WebApiUser>("WebApiUsers") {
    val id = long("id").primaryKey()
    val name = varchar("name")
    val password = text("password")
    val qid = long("qid")
    val authority = int("authority")
}