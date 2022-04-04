package com.xiaoyv404.mirai.databace.dao.gallery

import com.xiaoyv404.mirai.databace.Database.db
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar


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

/**
 * @return false 新增
 * @return true 更新
 */
fun Gallery.save(): Boolean {
    return if (this.findById() == null) {
        db.gallerys.add(this)
        false
    } else {
        this.update()
        true
    }
}

fun Gallery.update() {
    db.gallerys.update(this)
}

fun Gallery.findById(): Gallery? {
    return db.gallerys.find { it.id eq this.id }
}

fun Gallery.deleteById(){
    db.gallerys.removeIf { it.id eq this.id }
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