package com.xiaoyv404.mirai.databace.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long

interface Group : Entity<Group> {
    val id: Long
    val biliStatus: Int
    val eroStatus: Int
    val thesaurusStatus: Int
}

object Groups : Table<Group>("Groups") {
    val id = long("id").primaryKey()
    val biliStatus = int("biliStatus")
    val eroStatus = int("eroStatus")
    val thesaurusStatus = int("thesaurusStatus")
}
