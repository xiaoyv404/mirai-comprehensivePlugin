package com.xiaoyv404.mirai.app.accessControl

//import com.xiaoyv404.mirai.tool.jsonSearch
import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.Groups

fun authorityIdentification(uid: Long, gid: Long, func: String): Boolean {
    val gp = Groups.permission
    val sUid = uid.toString()

    Database.db.useConnection { conn ->
        val sql = """
SELECT 
	permission :: jsonb -> ? ->>'all',
	permission :: jsonb -> ? -> 'white' ?? ?,
	permission :: jsonb -> ? -> 'black' ?? ? 
FROM "Groups" 
WHERE "id" = ?""".trimIndent()
        conn.prepareStatement(sql).use { statement ->
            statement.setString(1, func)
            statement.setString(2, func)
            statement.setString(3, sUid)
            statement.setString(4, func)
            statement.setString(5, sUid)
            statement.setLong(6, gid)
            val eq = statement.executeQuery()
            return if (eq.next()) {
                if (eq.getBoolean(1)) {
                    eq.getString(3) != "t"
                } else
                    eq.getString(2) == "t"
            }else
                false
        }
    }
//    return Database.db
//        .from(Groups)
//        .select(
//            gp.jsonExtract(BooleanSqlType,func,"all"),
//            gp.jsonExtractContains(sUid, VarcharSqlType,func,"black"),
//            gp.jsonExtractContains(sUid, VarcharSqlType,func,"white"),
//        )
//        .where(Groups.id eq gid)
//        .map {
//            if (it.getString(1) == "true")
//                !it.getBoolean(2)
//            else
//                it.getBoolean(3)
//        }.first()
}


//fun permissionSearch(gid: Long, uid: Long, func: String): String? {
//    val sUid = uid.toString()
//    return Database.db
//        .from(Groups)
//        .select(
//            Groups.permission.jsonSearch("one", sUid, "$.$func", VarcharSqlType)
//        )
//        .where { Groups.id eq gid }
//        .map {
//            it.getString(1)
//        }.first()
//}
//
//fun permissionAllSet(gid: Long, func: String, switch: Boolean) {
//    Database.db.useConnection { conn ->
//        val sql = """
//            UPDATE `groups`
//            SET `permission` = JSON_SET(`permission`,CONCAT('$.',?,'.all'),?)
//            WHERE `id` = ?""".trimIndent()
//        conn.prepareStatement(sql).use { statement ->
//            statement.setString(1, func)
//            statement.setBoolean(2, switch)
//            statement.setLong(3, gid)
//        }
//    }
//}
//
//fun permissionListAdd(gid: Long, uid: Long, func: String) {
//    Database.db.useConnection { conn ->
//        val sql = """
//            UPDATE `groups`
//            SET `permission` = JSON_ARRAY_APPEND(`permission`, CONCAT('$.',?),?)
//            WHERE `id` = ?
//        """.trimIndent()
//        conn.prepareStatement(sql).use { statement ->
//            statement.setString(1, func)
//            statement.setString(2, uid.toString())
//            statement.setLong(3, gid)
//        }
//    }
//}
//
//fun permissionListRemove(gid: Long, uid: Long, func: String): Int {
//    val path = permissionSearch(gid,uid,func)
//    if (path.isNullOrBlank()){
//        return 0
//    }
//    Database.db.useConnection { conn ->
//        val sql = """
//            UPDATE `groups`
//            SET `permission` = JSON_REMOVE(`permission`,JSON_UNQUOTE(?))
//            WHERE `id` = ?
//        """.trimIndent()
//        conn.prepareStatement(sql).use { statement ->
//            statement.setString(1,path)
//            statement.setLong(2,gid)
//        }
//    }
//    return 1
//}