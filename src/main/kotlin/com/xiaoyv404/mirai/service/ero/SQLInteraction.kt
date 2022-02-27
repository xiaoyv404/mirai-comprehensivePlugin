package com.xiaoyv404.mirai.service.ero

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.gallery.GalleryTagMaps
import com.xiaoyv404.mirai.databace.dao.gallery.GalleryTags
import com.xiaoyv404.mirai.databace.dao.gallery.Gallerys
import org.ktorm.dsl.*


fun queryTagIdByTag(tag: String): Long? {
    return Database.db
        .from(GalleryTags)
        .select()
        .where { (GalleryTags.tagname eq tag) }
        .map { row ->  row[GalleryTags.tagid] }.first()
}

fun removeInformationById(id: Long) {
    Database.db
        .delete(GalleryTagMaps) { (GalleryTagMaps.pid eq id) }
    Database.db
        .delete(Gallerys) { (Gallerys.id eq id) }
}

fun queryTagQuantityByTagId(tagid: Long): Long {
    var data: Long = -1
    Database.db
        .from(GalleryTags)
        .select()
        .where { (GalleryTags.tagid eq tagid) }
        .forEach { data = it[GalleryTags.num]!! }
    return data
}
