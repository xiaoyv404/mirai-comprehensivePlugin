package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.databace.*
import com.xiaoyv404.mirai.model.gallery.*
import org.ktorm.dsl.*
import org.ktorm.entity.*

private val org.ktorm.database.Database.galleryTags get() = this.sequenceOf(GalleryTags)

fun GalleryTag.findByTagName(): GalleryTag? {
    return Database.db.galleryTags.find { it.tagname eq this.tagname }
}

fun GalleryTag.save(): Long {
    Database.db.galleryTags.add(this)
    return this.tagid
}

fun GalleryTag.update() {
    Database.db.galleryTags.update(this)
}

fun GalleryTag.findByTagId(): GalleryTag? {
    return Database.db.galleryTags.find { it.tagid eq this.tagid }
}

fun GalleryTag.findNumByTagId(): Long? {
    return this.findByTagId()?.num
}

fun GalleryTag.reduceNumByTagId() {
    val num = this.findNumByTagId() ?: return
    if (num > 0) {
        this.num = num - 1
        this.update()
    }
}

fun GalleryTag.findTagIdByTagName(): Long? {
    return this.findByTagName()?.tagid
}