@file:Suppress("SpellCheckingInspection")

package com.xiaoyv404.mirai.service

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.Groups
import com.xiaoyv404.mirai.databace.dao.Thesaurus
import com.xiaoyv404.mirai.databace.dao.Users
import org.ktorm.dsl.*
import org.ktorm.support.mysql.jsonExtract


data class User(
    val id: Long?,
    val admin: Boolean?,
    val bot: Boolean?,
    val setu: Boolean?,
)

data class Thesauru(
    val id: Long,
    val question: String,
    val reply: String,
    val creator: Long,
    val weight: Int
)

fun groupNoticeSwitchRead(gid: Long, func: String): Boolean {
    //执行操作并解析参数
    return Database.db
        .from(Groups)
        .select(
            Groups.notice.jsonExtract<Boolean>("$.$func")
        )
        .where(Groups.id eq gid)
        .map { it.getBoolean(1) }.first()
}





fun queryTerm(question: String): List<Thesauru> {
    return Database.db
        .from(Thesaurus)
        .select()
        .where { Thesaurus.question eq question }
        .map { row ->
            Thesauru(
                row[Thesaurus.id]!!,
                row[Thesaurus.question]!!,
                row[Thesaurus.reply]!!,
                row[Thesaurus.creator]!!,
                row[Thesaurus.weight]!!
            )
        }
}


fun getUserInformation(id: Long): User {
    return try {
        Database.db
            .from(Users)
            .select().where { (Users.id eq id) }
            .map { row -> User(row[Users.id], row[Users.admin], row[Users.bot], row[Users.setu]) }[0]
    } catch (e: IndexOutOfBoundsException) {
        User(null, null, null, null)
    }
}

fun createUserInformation(id: Long, admin: Boolean, bot: Boolean, setu: Boolean) {
    Database.db
        .insert(Users) {
            set(it.id, id)
            set(it.admin, admin)
            set(it.bot, bot)
            set(it.setu, setu)
        }
}

fun updateUserBot(id: Long, status: Boolean) {
    Database.db
        .update(Users) {
            set(it.setu, status)
            where {
                it.id eq id
            }
        }
}
