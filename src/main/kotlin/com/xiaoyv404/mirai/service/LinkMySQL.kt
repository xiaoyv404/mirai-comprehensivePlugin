package com.xiaoyv404.mirai.service

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.Groups
import com.xiaoyv404.mirai.databace.dao.Thesaurus
import com.xiaoyv404.mirai.databace.dao.Users
import org.ktorm.dsl.*

data class User(
    val id: Long?,
    val bot: Boolean?,
    val setu: Boolean?,
)

//Read
fun groupDataRead(id: Long): Boolean? {
    //执行操作并解析参数
    var data: Boolean? = null
    Database.db
        .from(Groups)
        .select()
        .where { (Groups.id eq id) }
        .forEach { row -> data = row[Groups.biliStatus] }
    return data
}

//Update
fun groupDataUpdate(id: Long, biliStatus: Boolean) {
    Database.db
        .update(Groups) {
            set(it.biliStatus, biliStatus)
            where {
                it.id eq id
            }
        }
}

//Create
fun groupDataCreate(id: Long) {
    Database.db
        .insert(Groups) {
            set(it.id, id)
            set(it.biliStatus, true)
        }
}

fun queryTerm(question: String): String {
    var data = ""
    Database.db
        .from(Thesaurus)
        .select()
        .where { (Thesaurus.question eq question) }
        .forEach { row -> data = row[Thesaurus.reply].toString() }
    return data
}


fun getUserInformation(id: Long): User {
    return try {
        Database.db
            .from(Users)
            .select().where { (Users.id eq id) }
            .map { row -> User(row[Users.id], row[Users.bot], row[Users.setu]) }[0]
    } catch (e: IndexOutOfBoundsException) {
        User(null, null, null)
    }
}

fun createUserInformation(id: Long, bot: Boolean, setu: Boolean) {
    Database.db
        .insert(Users) {
            set(it.id, id)
            set(it.bot, bot)
            set(it.setu, setu)
        }
}

fun updateUserSetu(id: Long, status: Boolean) {
    Database.db
        .update(Users) {
            set(it.setu, status)
            where {
                it.id eq id
            }
        }
}