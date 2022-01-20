package com.xiaoyv404.mirai.service.ero

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.gallery.GalleryTagMaps
import com.xiaoyv404.mirai.databace.dao.gallery.GalleryTags
import com.xiaoyv404.mirai.databace.dao.gallery.Gallerys
import com.xiaoyv404.mirai.service.ero.localGallery.ImageInfo
import com.xiaoyv404.mirai.service.ero.localGallery.Tag
import org.ktorm.dsl.*


fun queryTagIdByTag(tag: String): Long? {
    return Database.db
        .from(GalleryTags)
        .select()
        .where { (GalleryTags.tagname eq tag) }
        .map { row ->  row[GalleryTags.tagid] }.first()
}

fun queryIdByTagId(tagid: Long): List<Long> {
    return Database.db
        .from(GalleryTagMaps)
        .select()
        .where { GalleryTagMaps.tagid eq tagid }
        .map { row -> row[GalleryTagMaps.pid]!! }
}

fun getImgInformationById(id: Long): ImageInfo {
   return Database.db
        .from(Gallerys)
        .select()
        .where { Gallerys.id eq id }
        .map { row ->
            ImageInfo(
                row[Gallerys.id],
                row[Gallerys.picturesMun]?: 0,
                row[Gallerys.title],
                row[Gallerys.tags],
                row[Gallerys.userId],
                row[Gallerys.userName],
                row[Gallerys.extension]
            )
        }.first()
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
    da : ImageInfo,
    creator: Long,
    tagsL: List<Tag>,
) {
    Database.db
        .insert(Gallerys) {
            set(it.id, da.id)
            set(it.picturesMun, da.picturesNum)
            set(it.title, da.title)
            set(it.tags, da.tags)
            set(it.userId, da.userId)
            set(it.userName, da.userName)
            set(it.creator, creator)
            set(it.extension, da.extension)
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
                set(it.pid, da.id)
            }
    }
}

