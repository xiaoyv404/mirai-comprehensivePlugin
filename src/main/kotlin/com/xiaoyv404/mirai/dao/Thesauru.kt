package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.databace.*
import com.xiaoyv404.mirai.databace.entity.*
import com.xiaoyv404.mirai.entity.*
import com.xiaoyv404.mirai.extension.*
import org.ktorm.dsl.*
import org.ktorm.entity.*


private val org.ktorm.database.Database.thesauru get() = this.sequenceOf(Thesaurus)

/**
 * 更新或添加
 * @author xiaoyv_404
 * @create 2022/3/5
 *
 * @return true 添加 false 更新
 */
fun Thesauru.save(): Boolean {

    return if (this.findById() == null) {
        Database.db.thesauru.add(this)
        false
    } else {
        this.update()
        true
    }
}


fun Thesauru.update() {
    Database.db.thesauru.update(this)
}

fun Thesauru.findById(): Thesauru? {
    return Database.db.thesauru.find { it.id eq this.id }
}

/**
 * 使用问题查询回答
 * @author xiaoyv_404
 * @create 2022/3/5
 *
 * @param gid 群id[String]
 * @return List<回答>
 */
fun Thesauru.findByQuestion(gid: String): List<Thesauru> {
    return Database.db.thesauru.filter {
        it.question eq this.question and (it.scope.isNull() or (it.scope.asJson().findOrNot(gid)))
    }.toList()
}

/**
 * 使用问题查询回答
 * @author xiaoyv_404
 * @create 2022/3/5
 *
 * @param gid 群id[Long]
 * @return List<回答>
 */
fun Thesauru.findByQuestion(gid: Long):List<Thesauru>{
    return this.findByQuestion(gid.toString())
}

/**
 * 通过 ID 删除词条
 * @author xiaoyv_404
 * @create 2022/3/5
 *
 */
fun Thesauru.deleteById(){
    Database.db.thesauru.removeIf { it.id eq this.id }
}

