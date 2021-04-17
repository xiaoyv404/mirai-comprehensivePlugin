package com.xiaoyv404.mirai.databace.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.long

interface Group : Entity<Group> {
    val id: Long
    val biliStatus: Boolean
}

object Groups : Table<Group>("Groups") {
    val id = long("id").primaryKey()
    val biliStatus = boolean("biliStatus")
}
