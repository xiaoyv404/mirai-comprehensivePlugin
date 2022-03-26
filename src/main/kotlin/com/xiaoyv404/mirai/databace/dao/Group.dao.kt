package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.databace.Database.db
import com.xiaoyv404.mirai.extension.asJson
import com.xiaoyv404.mirai.extension.getAsString
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.Entity
import org.ktorm.entity.filter
import org.ktorm.entity.isNotEmpty
import org.ktorm.entity.sequenceOf
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

private val Database.group get() = this.sequenceOf(Groups)

fun Group.noticeSwitchRead(func: String): Boolean {
    return db.group.filter {
        it.id eq this.id and (it.notice.asJson().getAsString(func) eq "true")
    }.isNotEmpty()
}


object  Groups : Table<Group>("Groups") {
    val id = long("id").primaryKey().bindTo { it.id  }
    val notice = text("notice").bindTo { it.notice }
    val permission = text("permission").bindTo { it.permission }
    val salutatory = text("salutatory").bindTo { it.salutatory }
}
