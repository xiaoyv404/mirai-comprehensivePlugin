package com.xiaoyv404.mirai.service.ero

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.gallery.GalleryTagMaps
import com.xiaoyv404.mirai.databace.dao.gallery.GalleryTags
import com.xiaoyv404.mirai.databace.dao.gallery.Gallerys
import com.xiaoyv404.mirai.service.ero.localGallery.ImageInfo
import com.xiaoyv404.mirai.service.ero.localGallery.Tag
import org.ktorm.dsl.*


fun queryTagIdByTag(tag: String): Long {
    var tagid: Long = -1
    Database.db
        .from(GalleryTags)
        .select()
        .where { (GalleryTags.tagname eq tag) }
        .forEach { row -> tagid = row[GalleryTags.tagid]!! }
    return tagid
}

fun queryIdByTagId(tagid: Long): List<Long> {
    return Database.db
        .from(GalleryTagMaps)
        .select()
        .where { GalleryTagMaps.tagid eq tagid }
        .map { row -> row[GalleryTagMaps.pid]!! }
}

fun getImgInformationById(id: Long): ImageInfo {
    var data = ImageInfo(0, 0, "", "", 0, "", "")
    Database.db
        .from(Gallerys)
        .select()
        .where { Gallerys.id eq id }
        .forEach { row ->
            data = ImageInfo(
                row[Gallerys.id]!!,
                row[Gallerys.picturesMun]!!,
                row[Gallerys.title]!!,
                row[Gallerys.tags]!!,
                row[Gallerys.userId]!!,
                row[Gallerys.userName]!!,
                row[Gallerys.extension]!!
            )
        }
    return data
}

fun queryTagIdById(id: Long): List<Long> {
    return Database.db
        .from(GalleryTagMaps)
        .select()
        .where { (GalleryTagMaps.pid eq id) }
        .map { row -> row[GalleryTagMaps.tagid]!! }
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

fun updateTagNumber(tagid: Long, num: Long) {
    Database.db
        .update(GalleryTags) {
            set(it.num, num)
            where {
                it.tagid eq tagid
            }
        }
}


fun increaseEntry(
    id: Long,
    picturesMun: Int,
    title: String,
    tagsS: String,
    userId: Long,
    userName: String,
    creator: Long,
    tagsL: List<Tag>,
    extension: String
) {
    Database.db
        .insert(Gallerys) {
            set(it.id, id)
            set(it.picturesMun, picturesMun)
            set(it.title, title)
            set(it.tags, tagsS)
            set(it.userId, userId)
            set(it.userName, userName)
            set(it.creator, creator)
            set(it.extension, extension)
        }
    for (i in tagsL.indices) {
        var num: Long? = null
        var tagid: Long? = null
        Database.db
            .from(GalleryTags)
            .select()
            .where { (GalleryTags.tagname eq tagsL[i].tag) }
            .forEach { row ->
                num = row[GalleryTags.num]
                tagid = row[GalleryTags.tagid]
            }
        if (tagid != null) {
            Database.db
                .update(GalleryTags) {
                    set(it.num, num!! + 1)
                    where {
                        it.tagid eq tagid!!
                    }
                }
        } else {
            Database.db
                .insert(GalleryTags) {
                    set(it.num, 1)
                    set(it.tagname, tagsL[i].tag)
                }
            Database.db
                .from(GalleryTags)
                .select()
                .where { (GalleryTags.tagname eq tagsL[i].tag) }
                .forEach { row ->
                    tagid = row[GalleryTags.tagid]
                }
        }
        Database.db
            .insert(GalleryTagMaps) {
                set(it.tagid, tagid)
                set(it.pid, id)
            }
    }
}

