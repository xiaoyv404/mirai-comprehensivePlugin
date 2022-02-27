package com.xiaoyv404.mirai.databace.dao.gallery

import com.xiaoyv404.mirai.databace.Database.db
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.long

interface GalleryTagMap : Entity<GalleryTagMap> {
    companion object : Entity.Factory<GalleryTagMap>()

    var tagid: Long
    var pid: Long
}

private val Database.galleryTagMap get() = this.sequenceOf(GalleryTagMaps)

fun GalleryTagMap.save() {
    db.galleryTagMap.add(this)
}

fun GalleryTagMap.findTagIdByPid(): List<Long> {
    return db.galleryTagMap.filter { it.pid eq this.pid }.mapColumnsNotNull { it.tagid }.toList()
}

fun GalleryTagMap.findPidByTagId(): List<Long>{
    return db.galleryTagMap.filter { it.tagid eq this.tagid }.mapColumnsNotNull { it.pid }.toList()
}

fun GalleryTagMap.deleteByPid() {
    db.galleryTagMap.removeIf { it.pid eq this.pid }
}
object GalleryTagMaps : Table<GalleryTagMap>("Gallerys_TagMap") {
    val tagid = long("tagid").primaryKey().bindTo { it.tagid }
    val pid = long("pid").primaryKey().bindTo { it.pid }
}