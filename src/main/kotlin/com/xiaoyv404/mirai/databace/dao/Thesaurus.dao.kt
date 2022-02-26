package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.databace.Database.db
import com.xiaoyv404.mirai.service.tool.jsonExtractContains
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.isNull
import org.ktorm.dsl.or
import org.ktorm.entity.*
import org.ktorm.schema.*


interface Thesauru : Entity<Thesauru> {
    companion object : Entity.Factory<Thesauru>()

    var id: Long
    var question: String
    var reply: String
    var creator: Long
    var scope: String
}

private val Database.thesauru get() = this.sequenceOf(Thesaurus)

fun Thesauru.save(): Boolean {
    return if (this.findById() == null) {
        db.thesauru.add(this)
        false
    } else {
        this.update()
        true
    }
}

fun Thesauru.update() {
    db.thesauru.update(this)
}

fun Thesauru.findById(): Thesauru? {
    return db.thesauru.find { it.id eq this.id }
}

fun Thesauru.findByQuestion(gid: String): List<Thesauru> {
    return db.thesauru.filter {
        it.question eq this.question and it.scope.jsonExtractContains(
            "$",
            gid,
            VarcharSqlType
        ) or it.scope.isNull()
    }.toList()
}

fun Thesauru.findByQuestion(gid: Long):List<Thesauru>{
   return this.findByQuestion(gid.toString())
}

fun Thesauru.deleteById(){
    val target = db.thesauru.find { it.id eq this.id }?: return
    target.delete()
}

object Thesaurus : Table<Thesauru>("Thesaurus") {
    val id = long("id").primaryKey().bindTo { it.id }
    val question = varchar("question").bindTo { it.question }
    val reply = varchar("reply").bindTo { it.reply }
    val creator = long("creator").bindTo { it.creator }
    val scope = text("scope").bindTo { it.scope }
}
    