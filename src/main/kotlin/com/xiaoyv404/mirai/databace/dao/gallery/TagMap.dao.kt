package com.xiaoyv404.mirai.databace.dao.gallery

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long

interface GalleryTagMap : Entity<Gallery> {
    val tagid: Long
    val pid: Long
}

object GalleryTagMaps : Table<Gallery>("Gallerys_TagMap") {
    val tagid = long("tagid").primaryKey()
    val pid = long("pid").primaryKey()
}