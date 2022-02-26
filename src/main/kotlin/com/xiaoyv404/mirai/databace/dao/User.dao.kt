package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.databace.Database.db
import net.mamoe.mirai.contact.Member
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.long

interface User : Entity<User> {
    companion object : Entity.Factory<User>()
    var id: Long
    val admin: Boolean
    var bot: Boolean
    val setu: Boolean
}

private val Database.users get() = this.sequenceOf(Users)

/**
 * @return false 新增
 * @return true 更新
 */
fun User.save():Boolean{
    return if (this.findById() == null) {
        db.users.add(this)
        false
    } else {
        this.update()
        true
    }
}

fun User.update(){
    db.users.update(this)
}

fun User.findById(): User?{
    return db.users.find { it.id eq this.id }
}

fun User.itNotBot():Boolean{
    return this.findById()?.bot != true
}

fun User.itAdmin(): Boolean{
    return this.findById()?.admin == true
}

fun Member.itNotBot(): Boolean{
    return User{
        id = this@itNotBot.id
    }.itNotBot()
}

fun net.mamoe.mirai.contact.User.itNotBot(): Boolean {
    return User{
        id = this@itNotBot.id
    }.itNotBot()
}

fun net.mamoe.mirai.contact.User.itAdmin(): Boolean {
    return User{
        id = this@itAdmin.id
    }.itAdmin()
}


object Users : Table<User>("Users") {
    val id = long("id").primaryKey().bindTo { it.id }
    val admin = boolean("admin").bindTo { it.admin }
    val bot = boolean("bot").bindTo { it.bot }
    val setu = boolean("setu").bindTo { it.setu }
}