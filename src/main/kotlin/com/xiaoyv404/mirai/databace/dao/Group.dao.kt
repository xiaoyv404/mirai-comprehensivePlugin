package com.xiaoyv404.mirai.databace.dao

import kotlinx.serialization.json.Json
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.text

interface Group : Entity<Group> {
    val id: Long
    val notice: Json
    val permission: Json
    val salutatory: Json
}

object  Groups : Table<Group>("Groups") {
    val id = long("id").primaryKey()
    val notice = text("notice")
    val permission = text("permission")
    val salutatory = text("salutatory")
}
