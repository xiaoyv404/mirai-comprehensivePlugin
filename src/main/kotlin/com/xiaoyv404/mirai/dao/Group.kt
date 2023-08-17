package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.core.gid
import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.database.Database
import com.xiaoyv404.mirai.extension.asJson
import com.xiaoyv404.mirai.extension.findOrNot
import com.xiaoyv404.mirai.extension.get
import com.xiaoyv404.mirai.extension.getAsString
import com.xiaoyv404.mirai.model.Group
import com.xiaoyv404.mirai.model.GroupType
import com.xiaoyv404.mirai.model.Groups
import net.mamoe.mirai.event.events.MessageEvent
import org.ktorm.dsl.*
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.isNotEmpty
import org.ktorm.entity.sequenceOf

private val org.ktorm.database.Database.group get() = this.sequenceOf(Groups)

/**
 * 权限检查函数
 * @author xiaoyv_404
 * @create 2023/1/12
 *
 * @param uid
 * @param gid
 * @param func
 * @return True 无权限 False 有权限
 */
fun authorityIdentification(uid: Long, gid: Long, func: String): Boolean {
    val gp = Groups.permission
    val sUid = uid.toString()
    return Database.db
        .from(Groups)
        .select(
            gp.asJson()[func].getAsString("all"),
            gp.asJson()[func]["black"].findOrNot(sUid),
            gp.asJson()[func]["white"].findOrNot(sUid)
        )
        .where(Groups.id eq gid)
        .map {
            if (it.getString(1) == "true")
                it.getBoolean(2)
            else
                !it.getBoolean(3)
        }.first()
}

fun MessageEvent.authorityIdentification(func: String): Boolean {
    return authorityIdentification(this.uid(), this.gid(), func)
}

fun MessageEvent.groupType(): GroupType? {
    return Database.db.group.find { it.id eq gid() }?.type
}

fun Group.noticeSwitchRead(func: String): Boolean {
    return Database.db.group.filter {
        Groups.id eq this.id and (Groups.notice.asJson().getAsString(func) eq "true")
    }.isNotEmpty()
}