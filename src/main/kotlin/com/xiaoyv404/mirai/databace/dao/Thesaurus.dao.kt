package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.databace.Database.db
import com.xiaoyv404.mirai.tool.jsonExtractContains
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

object Thesaurus : Table<Thesauru>("Thesaurus") {
    val id = long("id").primaryKey().bindTo { it.id }
    val question = varchar("question").bindTo { it.question }
    val reply = varchar("reply").bindTo { it.reply }
    val creator = long("creator").bindTo { it.creator }
    val scope = text("scope").bindTo { it.scope }
}

private val Database.thesauru get() = this.sequenceOf(Thesaurus)

/**
 * ���»����
 * @author xiaoyv_404
 * @create 2022/3/5
 *
 * @return true ��� false ����
 */
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

/**
 * ʹ�������ѯ�ش�
 * @author xiaoyv_404
 * @create 2022/3/5
 *
 * @param gid Ⱥid[String]
 * @return List<�ش�>
 */
fun Thesauru.findByQuestion(gid: String): List<Thesauru> {
    return db.thesauru.filter {
        it.question eq this.question and (it.scope.jsonExtractContains(
            "$",
            gid,
            VarcharSqlType
        ) or it.scope.isNull())
    }.toList()
}

/**
 * ʹ�������ѯ�ش�
 * @author xiaoyv_404
 * @create 2022/3/5
 *
 * @param gid Ⱥid[Long]
 * @return List<�ش�>
 */
fun Thesauru.findByQuestion(gid: Long):List<Thesauru>{
   return this.findByQuestion(gid.toString())
}

/**
 * ͨ�� ID ɾ������
 * @author xiaoyv_404
 * @create 2022/3/5
 *
 */
fun Thesauru.deleteById(){
    db.thesauru.removeIf { it.id eq this.id }
}

