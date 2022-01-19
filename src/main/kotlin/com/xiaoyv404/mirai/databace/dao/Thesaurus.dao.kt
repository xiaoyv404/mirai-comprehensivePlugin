package com.xiaoyv404.mirai.databace.dao

import kotlinx.serialization.json.Json
import org.ktorm.entity.Entity
import org.ktorm.schema.*


interface Thesauru : Entity<Thesauru> {
    val id: Long
    val question: String
    val reply: String
    val creator: Long
    val scope: Json
}

object Thesaurus : Table<Thesauru>("Thesaurus") {
    val id = long("id").primaryKey()
    val question = varchar("question").primaryKey()
    val reply = varchar("reply")
    val creator = long("creator").primaryKey()
    val scope = text("scope")
}
    