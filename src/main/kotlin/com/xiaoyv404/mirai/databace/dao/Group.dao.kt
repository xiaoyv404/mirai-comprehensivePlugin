package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.databace.Database.db
import com.xiaoyv404.mirai.extension.*
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.or
import org.ktorm.entity.Entity
import org.ktorm.entity.filter
import org.ktorm.entity.isEmpty
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.TypeReference
import org.ktorm.schema.long
import org.ktorm.schema.text


interface Group : Entity<Group> {
    companion object : Entity.Factory<Group>()

    var id: Long
    val notice: Map<String, String>
    val permission: Map<String, ACD>
    val salutatory: String
}

private val Database.group get() = this.sequenceOf(Groups)

fun Group.test() {
    db.group.filter {
        it.permission.asJson(true)["BiliBiliParsing"].getAsString("all") eq "true"
    }
}

fun Group.noticeSwitchRead(func: String): Boolean {
    return db.group.filter {
        (it.notice.asJson(true).getAsString(func) eq "false") and (it.id eq this.id)
    }.isEmpty()
}

fun authorityIdentification(uid: Long, gid: Long, func: String): Boolean {
    val sUid = uid.toString()
    val sql = db.group.filter {
        (it.id eq gid) and ((it.permission.asJson(true)[func]["white"].findHaveOrNot(sUid) eq "t") or
            ((it.permission.asJson(true)[func].getAsString("all") eq "true") and
                (it.permission.asJson(true)[func]["black"].findHaveOrNot(sUid) eq "f")))
    }.sql
    println(sql)
    return db.group.filter {
        (it.id eq gid) and ((it.permission.asJson(true)[func]["white"].findHaveOrNot(sUid) eq "t") or
            ((it.permission.asJson(true)[func].getAsString("all") eq "true") and
                (it.permission.asJson(true)[func]["black"].findHaveOrNot(sUid) eq "f")))
    }.isEmpty()
}


object Groups : Table<Group>("Groups") {
    val id = long("id").primaryKey().bindTo { it.id }
    val notice = jsonb("notice", object : TypeReference<Map<String, String>>() {}).bindTo { it.notice }
    val permission = jsonb("permission", object : TypeReference<Map<String, ACD>>() {}).bindTo { it.permission }
    val salutatory = text("salutatory").bindTo { it.salutatory }
}

data class ACD(
    val all: Boolean,
    val black: List<String>,
    val controller: Controller,
    val white: List<String>
)

data class Controller(
    val groupAdmin: Boolean,
    val white: List<String>
)