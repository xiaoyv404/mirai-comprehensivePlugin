package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.databace.Database.db
import org.ktorm.dsl.*
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.text
import org.ktorm.support.mysql.jsonExtract

interface Group : Entity<Group> {
    companion object : Entity.Factory<Group>()
    var id: Long
    val notice: String
    val permission: String
    val salutatory: String
}

fun Group.noticeSwitchRead(func: String): Boolean {
    return db.from(Groups)
        .select(
            Groups.notice.jsonExtract<Boolean>("$.$func")
        )
        .where(Groups.id eq this.id)
        .map { it.getBoolean(1) }.first()
}


object  Groups : Table<Group>("Groups") {
    val id = long("id").primaryKey().bindTo { it.id  }

    // Todo 改用json类型
    val notice = text("notice").bindTo { it.notice }
    val permission = text("permission").bindTo { it.permission }
    val salutatory = text("salutatory").bindTo { it.salutatory }
}
