package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.databace.*
import com.xiaoyv404.mirai.entity.*
import net.mamoe.mirai.event.events.*
import org.ktorm.dsl.*
import org.ktorm.entity.*

private val org.ktorm.database.Database.users get() = this.sequenceOf(Users)

/**
 * @return false 新增
 * @return true 更新
 */
fun User.save():Boolean{
    return if (this.findById() == null) {
        Database.db.users.add(this)
        false
    } else {
        this.update()
        true
    }
}

fun User.update(){
    Database.db.users.update(this)
}

fun User.findById(): User?{
    return Database.db.users.find { it.id eq this.id }
}

fun User.isBot(): Boolean {
    return this.findById()?.bot == true
}

fun User.isNotAdmin(): Boolean {
    return this.findById()?.admin == false
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

fun MessageEvent.isNotAdmin(): Boolean {
    return this@isNotAdmin.uid().isNotAdmin()
}