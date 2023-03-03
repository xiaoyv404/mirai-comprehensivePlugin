package com.xiaoyv404.mirai.entity.gallery

import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.long

interface GalleryTagMap : Entity<GalleryTagMap> {
    companion object : Entity.Factory<GalleryTagMap>()

    var tagid: Long
    var pid: Long
}

object GalleryTagMaps : Table<GalleryTagMap>("Gallerys_TagMap") {
    val tagid = long("tagid").primaryKey().bindTo { it.tagid }
    val pid = long("pid").primaryKey().bindTo { it.pid }
}