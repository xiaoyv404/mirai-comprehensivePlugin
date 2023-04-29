package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.databace.*
import com.xiaoyv404.mirai.model.gallery.*
import org.ktorm.dsl.*
import org.ktorm.entity.*

private val org.ktorm.database.Database.gallerys get() = this.sequenceOf(Gallerys)

/**
 * @return false 新增
 * @return true 更新
 */
fun Gallery.save(): Boolean {
    return if (this.findById() == null) {
        Database.db.gallerys.add(this)
        false
    } else {
        this.update()
        true
    }
}

fun Gallery.update() {
    Database.db.gallerys.update(this)
}

fun Gallery.findById(): Gallery? {
    return Database.db.gallerys.find { it.id eq this.id }
}

fun Gallery.deleteById(){
    Database.db.gallerys.removeIf { it.id eq this.id }
}
