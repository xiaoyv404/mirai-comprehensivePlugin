package com.xiaoyv404.mirai.model

import com.xiaoyv404.mirai.extension.jsonb
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.TypeReference
import org.ktorm.schema.long
import org.ktorm.schema.varchar


interface Thesauru : Entity<Thesauru> {
    companion object : Entity.Factory<Thesauru>()
    var id: Long
    var question: String
    var reply: String
    var creator: Long
    var scope: List<String>
}

object Thesaurus : Table<Thesauru>("Thesaurus") {
    val id = long("id").primaryKey().bindTo { it.id }
    val question = varchar("question").bindTo { it.question }
    val reply = varchar("reply").bindTo { it.reply }
    val creator = long("creator").bindTo { it.creator }
    val scope = jsonb("scope", object : TypeReference<List<String>>() {}).bindTo { it.scope }
}
