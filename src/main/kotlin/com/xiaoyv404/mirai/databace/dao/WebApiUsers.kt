package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.databace.Database.db
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.*
import org.mindrot.jbcrypt.BCrypt

interface WebApiUser : Entity<WebApiUser> {
    companion object : Entity.Factory<WebApiUser>()

    val id: Long
    var name: String
    var password: String
    var qid: Long
    val authority: Int
}

private val Database.webApiUser get() = this.sequenceOf(WebApiUsers)

/**
 * 通过用户名查找用户
 * @author xiaoyv_404
 * @create 2022/2/27
 *
 * @return WebApiUser?
 */
fun WebApiUser.findByName(): WebApiUser? {
    return db.webApiUser.find { it.name eq this.name }
}

/**
 * 更新用户数据
 * @author xiaoyv_404
 * @create 2022/2/27
 *
 */
fun WebApiUser.update(){
    db.webApiUser.update(this)
}

/**
 * 添加用户
 * @author xiaoyv_404
 * @create 2022/2/27
 *
 * @return WebApiUser
 */
fun WebApiUser.add(): WebApiUser {
    this.password = BCrypt.hashpw(this.password, BCrypt.gensalt())
    db.webApiUser.add(this)
    return this
}

fun WebApiUser.save(){
    val target = this.findByName()
    if (target == null){
        this.add()
    }else{
        this.update()
    }
}

/**
 * 查询或添加用户
 * @author xiaoyv_404
 * @create 2022/2/27
 *
 * @return WebApiUser
 */
fun WebApiUser.findByNameOrSave(): WebApiUser {
   return this.findByName()?: this.add()
}


/**
 * 判断是否是WebAdmin
 * @author xiaoyv_404
 * @create 2022/2/27
 *
 * @return Boolean
 */
fun String.itWebAdmin(): Boolean {
    return WebApiUser {
        name = this@itWebAdmin
    }.findByName()?.authority == 1
}

/**
 * 权限需要 Admin
 * @author xiaoyv_404
 * @create 2022/2/27
 *
 * @throws "No permission"
 */
fun String.permissionRequiredAdmin(){
    if (!this.itWebAdmin()) {
        error("No permission")
    }
}

object WebApiUsers : Table<WebApiUser>("WebApiUsers") {
    val id = long("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val password = text("password").bindTo { it.password }
    val qid = long("qid").bindTo { it.qid }
    val authority = int("authority").bindTo { it.authority }
}