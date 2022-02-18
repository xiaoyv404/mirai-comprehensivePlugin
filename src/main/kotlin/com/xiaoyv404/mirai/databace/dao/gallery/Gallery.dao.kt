package com.xiaoyv404.mirai.databace.dao.gallery

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar
import com.xiaoyv404.mirai.databace.Database as DB


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

private val Database.gallerys get() = this.sequenceOf(Gallerys)
fun Gallery.save(){
    DB.db.gallerys.add(this)
}


object Gallerys : Table<Gallery>("Gallerys") {
    val id = long("id").primaryKey().bindTo { it.id }
    val title = varchar("title").bindTo { it.title }
    val picturesMun = int("picturesMun").bindTo { it.picturesMun }
    val tags = varchar("tags").primaryKey().bindTo { it.tags }
    val userId = long("userId").bindTo { it.userId }
    val userName = varchar("userName").bindTo { it.userName }
    val creator = long("creator").bindTo { it.creator }
    val extension = varchar("extension").bindTo { it.extension }
}