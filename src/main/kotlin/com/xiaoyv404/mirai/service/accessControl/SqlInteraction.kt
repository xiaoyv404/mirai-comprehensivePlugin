package com.xiaoyv404.mirai.service.accessControl

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.Groups
import com.xiaoyv404.mirai.service.tool.jsonExtractContains
import org.ktorm.dsl.*
import org.ktorm.schema.LongSqlType
import org.ktorm.support.mysql.jsonExtract

fun authorityIdentification(uid: Long, gid: Long, func: String): Boolean {
    val gp = Groups.permission
    return Database.db
        .from(Groups)
        .select(
            gp.jsonExtract<Boolean>("$.$func.all"),
            gp.jsonExtractContains("$.$func.black", uid, LongSqlType),
            gp.jsonExtractContains("$.$func.white", uid, LongSqlType),
        )
        .where(Groups.id eq gid)
        .map {
            if (it.getString(1) == "true")
                !it.getBoolean(2)
            else
                it.getBoolean(3)
        }.first()
}


fun permissionAllSet(gid: Long, func: String, switch: Boolean) {
    Database.db.useConnection { conn ->
        val sql = """
            UPDATE `groups`
            SET `permission` = JSON_SET(`permission`,CONCAT('$.',?,'.all'),?)
            WHERE `id` = ?""".trimIndent()
        conn.prepareStatement(sql).use { statement ->
            statement.setString(1, func)
            statement.setBoolean(2, switch)
            statement.setLong(3, gid)
        }
    }
}
fun permissionListAdd(gid: Long,uid: Long, func: String) {
    Database.db.useConnection { conn ->
        val sql = """
            UPDATE `groups` 
            SET `permission` = JSON_ARRAY_APPEND(`permission`, CONCAT('$.',?),?)
            WHERE `id` = ?  
        """.trimIndent()
        conn.prepareStatement(sql).use { statement ->
            statement.setString(1, func)
            statement.setLong(2, uid)
            statement.setLong(3, gid)
        }
    }
}

fun permissionListRemove(gid: Long,uid: Long, func: String){
    Database.db.useConnection { conn ->
        val sql = """
            UPDATE `groups` 
            SET `permission` = JSON_REMOVE(JSON_CONTAINS(JSON_EXTRACT(`permission`,CONCAT('${'$'}.','ThesaurusResponse','.black[*]')),JSON_ARRAY(2083664136)))
            WHERE `id` = ?
        """.trimIndent()
        conn.prepareStatement(sql).use { statement ->

        }
    }
}//todo Î´Íê³É