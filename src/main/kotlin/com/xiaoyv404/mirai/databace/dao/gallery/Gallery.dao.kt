package com.xiaoyv404.mirai.databace.dao.gallery

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
    val userId: Long
    val userName: String
    val creator: Long
    val extension: String
}

object Gallerys : Table<Gallery>("Gallerys") {
    val id = long("id").primaryKey()
    val title = varchar("title")
    val picturesMun = int("picturesMun")
    val tags = varchar("tags").primaryKey()
    val userId = long("userId")
    val userName = varchar("userName")
    val creator = long("creator")
    val extension = varchar("extension")
}
