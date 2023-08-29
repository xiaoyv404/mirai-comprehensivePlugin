package com.xiaoyv404.mirai.model

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.enum
import org.ktorm.schema.long
import java.time.LocalDateTime

interface UserAlertLog : Entity<UserAlertLog> {
    val target: Long
    val executor: Long
    val time: LocalDateTime
    val type: UserAlertType
}

enum class UserAlertType {
    Rest,
    Decrease,
    Increase
}

object UserAlertLogs : Table<UserAlertLog>("UserAlertLogs") {
    val target = long("target").bindTo { it.target }
    val executor = long("executor").bindTo { it.executor }
    val time = datetime("time").bindTo { it.time }
    val type = enum<UserAlertType>("type").bindTo { it.type }
}