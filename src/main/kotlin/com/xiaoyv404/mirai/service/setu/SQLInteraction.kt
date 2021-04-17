package com.xiaoyv404.mirai.service.setu

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.Gallerys
import org.ktorm.dsl.insert

fun queryByTag(tags: String): ImageInfo {
    var data = ImageInfo(0, 0, "", "", "", "")
    Database.db.useConnection { conn ->
        val sql = """
                select * from Gallerys WHERE MATCH(tags) AGAINST(?) AND id <> 1 ORDER BY RAND() LIMIT 1
            """
        conn.prepareStatement(sql).use { statement ->
            statement.setString(1, tags)
            val setus = statement.executeQuery()
            while (setus.next()) {
                data = ImageInfo(
                    setus.getLong(1),
                    setus.getInt(2),
                    setus.getString(3),
                    setus.getString(4),
                    setus.getString(5),
                    setus.getString(6)
                )
            }
        }
    }
    return data
}

fun increaseEntry(
    id: Long,
    picturesMun: Int,
    title: String,
    tags: String,
    userId: Long,
    userName: String,
    creator: Long,
) {
    Database.db
        .insert(Gallerys) {
            set(it.id, id)
            set(it.picturesMun, picturesMun)
            set(it.title, title)
            set(it.tags, tags)
            set(it.userId, userId)
            set(it.userName, userName)
            set(it.creator, creator)
        }
}