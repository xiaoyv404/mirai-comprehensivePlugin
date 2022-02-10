package com.xiaoyv404.mirai.service.ero

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.gallery.GalleryTagMaps
import com.xiaoyv404.mirai.databace.dao.gallery.GalleryTags
import com.xiaoyv404.mirai.databace.dao.gallery.Gallerys
import com.xiaoyv404.mirai.service.ero.localGallery.LocalGallery
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

fun getImgInformationById(id: Long): LocalGallery.Process.Img.Info {
   return Database.db
        .from(Gallerys)
        .select()
        .where { Gallerys.id eq id }
        .map { row ->
            LocalGallery.Process.Img.Info(
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


object SQLInteraction {
    object Gallerys {
        fun insert(
            da: LocalGallery.Process.Img.Info,
            creator: Long,
        ) {
            Database.db
                .insert(com.xiaoyv404.mirai.databace.dao.gallery.Gallerys) {
                    set(it.id, da.id)
                    set(it.picturesMun, da.picturesNum)
                    set(it.title, da.title)
                    set(it.tags, da.tags)
                    set(it.userId, da.userId)
                    set(it.userName, da.userName)
                    set(it.creator, creator)
                    set(it.extension, da.extension)
                }
        }
    }
}

