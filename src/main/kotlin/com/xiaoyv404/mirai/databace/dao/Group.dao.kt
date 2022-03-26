package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.databace.Database.db
import com.xiaoyv404.mirai.extension.asJson
import com.xiaoyv404.mirai.extension.findOrNot
import com.xiaoyv404.mirai.extension.get
import com.xiaoyv404.mirai.extension.getAsString
import org.ktorm.database.Database
import org.ktorm.dsl.*
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

fun authorityIdentification(uid: Long, gid: Long, func: String): Boolean {
    val gp = Groups.permission
    val sUid = uid.toString()
    return db
        .from(Groups)
        .select(
            gp.asJson()[func].getAsString("all"),
            gp.asJson()[func]["white"].findOrNot(sUid),
            gp.asJson()[func]["white"].findOrNot(sUid)
        )
        .where(Groups.id eq gid)
        .map {
            if (it.getString(1) == "true")
                !it.getBoolean(2)
            else
                it.getBoolean(3)
        }.first()
}

object  Groups : Table<Group>("Groups") {
    val id = long("id").primaryKey().bindTo { it.id  }
    val notice = text("notice").bindTo { it.notice }
    val permission = text("permission").bindTo { it.permission }
    val salutatory = text("salutatory").bindTo { it.salutatory }
}
