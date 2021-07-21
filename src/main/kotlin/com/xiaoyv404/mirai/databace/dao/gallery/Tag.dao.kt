package com.xiaoyv404.mirai.databace.dao.gallery

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

interface GalleryTag : Entity<Gallery> {
    val tagid: Long
    val tagname: String
    val num: Long
}

object GalleryTags : Table<Gallery>("Gallerys_Tag") {
    val tagid = long("tagid")
    val tagname = varchar("tagname").primaryKey()
    val num = long("num")
}
