package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.database.Database
import com.xiaoyv404.mirai.model.UserAlertLog
import com.xiaoyv404.mirai.model.UserAlertLogs
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList

private val org.ktorm.database.Database.userAlertLogs get() = this.sequenceOf(UserAlertLogs)


fun UserAlertLog.add() {
    Database.db.userAlertLogs.add(this)
}

fun UserAlertLog.findByID(): List<UserAlertLog> {
    return Database.db.userAlertLogs
        .filter { it.target eq this.target }
        .toList()
}