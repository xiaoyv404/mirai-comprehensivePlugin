package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.database.Database
import com.xiaoyv404.mirai.model.User
import com.xiaoyv404.mirai.model.Users
import net.mamoe.mirai.event.events.MessageEvent
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.update

private val org.ktorm.database.Database.users get() = this.sequenceOf(Users)

/**
 * @return false 新增
 * @return true 更新
 */
fun User.save(): Boolean {
    return if (this.findById() == null) {
        Database.db.users.add(this)
        false
    } else {
        this.update()
        true
    }
}

fun User.update() {
    Database.db.users.update(this)
}

fun User.findById(): User? {
    return Database.db.users.find { it.id eq this.id }
}

fun User.isBot(): Boolean {
    return this.findById()?.bot == true
}

fun User.isNotAdmin(): Boolean {
    return this.findById()?.admin != true
}

fun Long.isNotAdmin(): Boolean {
    return User {
        id = this@isNotAdmin
    }.isNotAdmin()
}

fun Long.isBot(): Boolean {
    return User {
        id = this@isBot
    }.isBot()
}

fun MessageEvent.isBot(): Boolean {
    return this.uid().isBot()
}

fun MessageEvent.isNotAdmin(): Boolean {
    return this@isNotAdmin.uid().isNotAdmin()
}