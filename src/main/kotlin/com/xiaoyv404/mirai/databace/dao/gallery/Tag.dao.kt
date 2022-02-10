package com.xiaoyv404.mirai.databace.dao.gallery

import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar
import com.xiaoyv404.mirai.databace.Database as DB

private val Database.galleryTags get() = this.sequenceOf(GalleryTags)

interface GalleryTag : Entity<GalleryTag> {
    companion object : Entity.Factory<GalleryTag>()
    var tagid: Long
    var tagname: String
    var num: Long

    fun findByTagName(tagname: String): GalleryTag? {
        return DB.db.galleryTags.find { it.tagname eq tagname }
    }
}
fun GalleryTag.save(): Long {
    DB.db.galleryTags.add(this)
    return this.tagid
}
fun GalleryTag.update(){
    DB.db.galleryTags.update(this)
}




object GalleryTags : Table<GalleryTag>("Gallerys_Tag_Test") {
    val tagid = long("tagid").primaryKey().bindTo{it.tagid}
    val tagname = varchar("tagname").bindTo { it.tagname }
    val num = long("num").bindTo { it.num }
}

