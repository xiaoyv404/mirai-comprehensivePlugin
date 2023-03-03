package com.xiaoyv404.mirai.entity.gallery

import org.ktorm.entity.*
import org.ktorm.schema.*


interface Gallery : Entity<Gallery> {
    companion object : Entity.Factory<Gallery>()
    var id: Long
    var picturesMun: Int
    var title: String
    var tags: String
    var userId: Long
    var userName: String
    var creator: Long
    var extension: String
}



object Gallerys : Table<Gallery>("Gallerys") {
    val id = long("id").primaryKey().bindTo { it.id }
    val title = varchar("title").bindTo { it.title }
    val picturesMun = int("picturesMun").bindTo { it.picturesMun }
    val tags = varchar("tags").bindTo { it.tags }
    val userId = long("userId").bindTo { it.userId }
    val userName = varchar("userName").bindTo { it.userName }
    val creator = long("creator").bindTo { it.creator }
    val extension = varchar("extension").bindTo { it.extension }
}