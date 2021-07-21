package com.xiaoyv404.mirai.service

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.Groups
import com.xiaoyv404.mirai.databace.dao.Thesaurus
import com.xiaoyv404.mirai.databace.dao.Users
import org.ktorm.dsl.*

data class User(
    val id: Long?,
    val admin: Boolean?,
    val bot: Boolean?,
    val setu: Boolean?,
)

data class Group(
    val id: Long?,
    val biliStatus: Int?,
    val eroStatus: Int?,
    val thesaurusStatus: Int?
)

data class Thesauru(
    val id: Int?,
    val question: String?,
    val reply: String?,
    val creator: Long?,
    val weight: Int?
)

//Read
fun groupDataRead(id: Long): List<Group> {
    //执行操作并解析参数
    return Database.db
        .from(Groups)
        .select()
        .where { (Groups.id eq id) }
        .map { row -> Group(0, row[Groups.biliStatus], row[Groups.eroStatus], row[Groups.thesaurusStatus]) }
}

//Update
fun groupDataUpdate(id: Long, biliStatus: Int) {
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
            set(it.biliStatus, 1)
        }
}

fun queryTerm(question: String): List<Thesauru> {
    return Database.db
        .from(Thesaurus)
        .select()
        .where { Thesaurus.question eq question }
        .map { row -> Thesauru(null, null, row[Thesaurus.reply], null, row[Thesaurus.weight]) }
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