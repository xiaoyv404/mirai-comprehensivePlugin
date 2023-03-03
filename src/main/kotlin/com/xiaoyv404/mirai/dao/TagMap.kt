package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.databace.*
import com.xiaoyv404.mirai.entity.gallery.*
import org.ktorm.dsl.*
import org.ktorm.entity.*


private val org.ktorm.database.Database.galleryTagMap get() = this.sequenceOf(GalleryTagMaps)

fun GalleryTagMap.save() {
    Database.db.galleryTagMap.add(this)
}

fun GalleryTagMap.findTagIdByPid(): List<Long> {
    return Database.db.galleryTagMap.filter { it.pid eq this.pid }.mapColumnsNotNull { it.tagid }.toList()
}

fun GalleryTagMap.findPidByTagId(): List<Long>{
    return Database.db.galleryTagMap.filter { it.tagid eq this.tagid }.mapColumnsNotNull { it.pid }.toList()
}

fun GalleryTagMap.deleteByPid() {
    Database.db.galleryTagMap.removeIf { it.pid eq this.pid }
}