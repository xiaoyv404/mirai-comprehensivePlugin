package com.xiaoyv404.mirai.databace.dao.gallery

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.long
import com.xiaoyv404.mirai.databace.Database as DB

interface GalleryTagMap : Entity<GalleryTagMap> {
    companion object : Entity.Factory<GalleryTagMap>()
    var tagid: Long
    var pid: Long
}

private val Database.galleryTagMap get() = this.sequenceOf(GalleryTagMaps)
fun GalleryTagMap.save(){
    DB.db.galleryTagMap.add(this)
}

object GalleryTagMaps : Table<GalleryTagMap>("Gallerys_TagMap") {
    val tagid = long("tagid").primaryKey().bindTo { it.tagid }
    val pid = long("pid").primaryKey().bindTo { it.pid }
}