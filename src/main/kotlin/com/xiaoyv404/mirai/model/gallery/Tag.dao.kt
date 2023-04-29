package com.xiaoyv404.mirai.model.gallery

import org.ktorm.entity.*
import org.ktorm.schema.*


interface GalleryTag : Entity<GalleryTag> {
    companion object : Entity.Factory<GalleryTag>()
    var tagid: Long
    var tagname: String
    var num: Long
}


object GalleryTags : Table<GalleryTag>("Gallerys_Tag") {
    val tagid = long("tagid").primaryKey().bindTo{it.tagid}
    val tagname = varchar("tagname").bindTo { it.tagname }
    val num = long("num").bindTo { it.num }
}

