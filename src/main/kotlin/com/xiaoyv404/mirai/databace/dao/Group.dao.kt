package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.databace.Database.db
import com.xiaoyv404.mirai.tool.jsonExtract
import org.ktorm.dsl.*
import org.ktorm.entity.Entity
import org.ktorm.schema.BooleanSqlType
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.text

interface Group : Entity<Group> {
    companion object : Entity.Factory<Group>()
    var id: Long
    val notice: String
    val permission: String
    val salutatory: String
}

//private val Database.group get() = this.sequenceOf(Groups)

fun Group.noticeSwitchRead(func: String): Boolean {
    return db.from(Groups)
        .select(
            Groups.notice.jsonExtract(BooleanSqlType,func)
        )
        .where(Groups.id eq this.id)
        .map { it.getBoolean(1) }.first()
}


object  Groups : Table<Group>("Groups") {
    val id = long("id").primaryKey().bindTo { it.id  }
    val notice = text("notice").bindTo { it.notice }
    val permission = text("permission").bindTo { it.permission }
    val salutatory = text("salutatory").bindTo { it.salutatory }
}
