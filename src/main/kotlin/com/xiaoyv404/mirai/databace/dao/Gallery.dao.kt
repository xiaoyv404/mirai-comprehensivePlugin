package com.xiaoyv404.mirai.databace.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

interface Gallery : Entity<Gallery> {
    val id: Long
    val picturesMun: Int
    val title: String
    val tags: String
    val userId: String
    val userName: String
    val creator: Long
}

object Gallerys : Table<Gallery>("Gallerys") {
    val id = long("id").primaryKey()
    val title = varchar("title")
    val picturesMun = int("picturesMun")
    val tags = varchar("tags").primaryKey()
    val userId = long("userId")
    val userName = varchar("userName")
    val creator = long("creator")
}
