package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.databace.*
import com.xiaoyv404.mirai.model.*
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.mindrot.jbcrypt.*

private val org.ktorm.database.Database.webApiUser get() = this.sequenceOf(WebApiUsers)

/**
 * 通过用户名查找用户
 * @author xiaoyv_404
 * @create 2022/2/27
 *
 * @return WebApiUser?
 */
fun WebApiUser.findByName(): WebApiUser? {
    return Database.db.webApiUser.find { it.name eq this.name }
}

/**
 * 更新用户数据
 * @author xiaoyv_404
 * @create 2022/2/27
 *
 */
fun WebApiUser.update(){
    Database.db.webApiUser.update(this)
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
    Database.db.webApiUser.add(this)
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
