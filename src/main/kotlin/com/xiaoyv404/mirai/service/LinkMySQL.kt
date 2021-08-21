@file:Suppress("SpellCheckingInspection")

package com.xiaoyv404.mirai.service

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.Groups
import com.xiaoyv404.mirai.databace.dao.Groups.permission
import com.xiaoyv404.mirai.databace.dao.Thesaurus
import com.xiaoyv404.mirai.databace.dao.Users
import org.ktorm.database.asIterable
import org.ktorm.dsl.*
import org.ktorm.expression.ArgumentExpression
import org.ktorm.expression.FunctionExpression
import org.ktorm.schema.*
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

fun groupNoticeSwitchRead(id: Long, func: String): Boolean {
    //执行操作并解析参数
    return Database.db.useConnection { conn ->
        val sql = """
SELECT
    JSON_EXTRACT(`notice`, ?)
FROM `groups` WHERE `id` = ?"""
        conn.prepareStatement(sql).use { statement ->
            statement.setString(1, "\$.$func")
            statement.setLong(2, id)
            statement.executeQuery().asIterable().map { it.getBoolean(1) }
        }
    }.first()
}

fun permissionRead(uid: Long, gid: Long, func: String): Boolean {
       return Database.db
            .from(Groups)
            .select(
                permission.jsonExtract<Boolean>("$.$func.all"),
                permission.jsonExtractContains("$.$func.black", uid, LongSqlType),
                permission.jsonExtractContains("$.$func.white", uid, LongSqlType),
            )
            .where(Groups.id eq gid)
            .map {
                if (it.getString(1) == "true")
                    !it.getBoolean(2)
                else
                    it.getBoolean(3)
            }.first()
}

inline fun <reified T : Any> Column<*>.jsonExtractContains(
    path: String,
    item: T,
    sqlType: SqlType<T>
): FunctionExpression<Boolean> {
    val listSqlType = this.sqlType

    return FunctionExpression(
        functionName = "json_contains",
        arguments = listOf(
            FunctionExpression(
                functionName = "json_extract",
                arguments = listOf(asExpression(), ArgumentExpression(path, VarcharSqlType)),
                sqlType = sqlType
            ),
            FunctionExpression(
                functionName = "json_array",
                arguments = listOf(ArgumentExpression(item, sqlType)),
                sqlType = listSqlType
            )
        ),
        sqlType = BooleanSqlType
    )
}

fun permissionAllSet(gid: Long, func: String, switch: Boolean) {
    Database.db.useConnection { conn ->
        val sql = """
UPDATE `groups`
SET `permission` = JSON_SET(`permission`,CONCAT('$.',?,'.all'),?)
WHERE `id` = ?"""
        conn.prepareStatement(sql).use { statement ->
            statement.setString(1, func)
            statement.setBoolean(2, switch)
            statement.setLong(3, gid)
        }
    }
}

fun authorityIdentification(gid: Long, uid: Long, func: String) {
    println(Database.db
        .from(Groups)
        .select(permission.jsonExtract<Boolean>("$.ThesaurusResponse.all"))
        .where(Users.id eq 862315052)
        .map { it }
        .first())
}

fun test(gid: Long, uid: Long, func: String) {
    println(
        Database.db
            .from(Groups)
            .select(permission.jsonExtract<List<Long>>("$.ThesaurusResponse.black"))
    )
}


////Update
//fun groupDataUpdate(id: Long, biliStatus: Int) {
//    Database.db
//        .update(Groups) {
//            set(it.biliStatus, biliStatus)
//            where {
//                it.id eq id
//            }
//        }
//}
//
////Create
//fun groupDataCreate(id: Long) {
//    Database.db
//        .insert(Groups) {
//            set(it.id, id)
//            set(it.biliStatus, 1)
//        }
//}

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
