package com.xiaoyv404.mirai.databace.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar


interface Thesauru : Entity<Thesauru> {
    val id: Int
    val question: String
    val reply: String
    val creator: Long
    val weight: Int
}

object Thesaurus : Table<Thesauru>("Thesaurus") {
    val id = int("id").primaryKey()
    val question = varchar("question").primaryKey()
    val reply = varchar("reply")
    val creator = long("creator").primaryKey()
    val weight = int("weight")
}
    