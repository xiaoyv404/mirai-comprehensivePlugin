package com.xiaoyv404.mirai.model

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.enum
import org.ktorm.schema.long
import java.time.LocalDateTime

interface UserAlertLog : Entity<UserAlertLog> {
    companion object : Entity.Factory<UserAlertLog>()

    var target: Long
    var executor: Long
    var time: LocalDateTime
    var type: UserAlertType
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