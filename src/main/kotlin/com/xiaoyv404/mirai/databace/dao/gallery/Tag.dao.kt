package com.xiaoyv404.mirai.databace.dao.gallery

import com.xiaoyv404.mirai.databace.Database.db
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

private val Database.galleryTags get() = this.sequenceOf(GalleryTags)

interface GalleryTag : Entity<GalleryTag> {
    companion object : Entity.Factory<GalleryTag>()
    var tagid: Long
    var tagname: String
    var num: Long
}
fun GalleryTag.findByTagName(): GalleryTag?{
    return db.galleryTags.find { it.tagname eq this.tagname }
}

fun GalleryTag.save(): Long {
    db.galleryTags.add(this)
    return this.tagid
}

fun GalleryTag.update() {
    db.galleryTags.update(this)
}

fun GalleryTag.findByTagId(): GalleryTag? {
    return db.galleryTags.find { it.tagid eq this.tagid }
}

fun GalleryTag.findNumByTagId(): Long? {
    return this.findByTagId()?.num
}

fun GalleryTag.reduceNumByTagId() {
    val num = this.findNumByTagId() ?: return
    if (num > 0) {
        this.num = num-1
        println(this.num)
        this.update()
    }
}


object GalleryTags : Table<GalleryTag>("Gallerys_Tag") {
    val tagid = long("tagid").primaryKey().bindTo{it.tagid}
    val tagname = varchar("tagname").bindTo { it.tagname }
    val num = long("num").bindTo { it.num }
}

