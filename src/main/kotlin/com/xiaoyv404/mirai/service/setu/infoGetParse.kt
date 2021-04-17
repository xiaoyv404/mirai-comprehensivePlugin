package com.xiaoyv404.mirai.service.setu

import com.xiaoyv404.mirai.PluginConfig
import com.xiaoyv404.mirai.databace.Pixiv
import com.xiaoyv404.mirai.service.tool.FileUtils
import com.xiaoyv404.mirai.service.tool.dataGet
import com.xiaoyv404.mirai.service.tool.downloadImage
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.sql.SQLIntegrityConstraintViolationException

val format = Json { ignoreUnknownKeys = true }
val regular = Pixiv()
var num = 1

@KtorExperimentalAPI
suspend fun unformat(id: Long, senderId: Long): ImageInfo {
    val formatInfo = downloadPixivPage(id)

    return if (formatInfo == "") {
        ImageInfo(0, 0, "", "", "", "")
    } else {
        val pJ = format.decodeFromString<PixivJson>(
            regular.worksInfoFind
                .find(formatInfo)!!.value + "}"
        )


        var tags = ""
        pJ.tags.tags.forEach {
            tags += it.tag + ","
        }

        try {
            val `in`: InputStream = downloadImage("https://pixiv.cat/$id.png")!!

            FileUtils.saveFileFromStream(`in`, File("${PluginConfig.database.SaveAddress}$id.png"))
        } catch (e: Exception) {
            num = regular.worksNumberFind.find(dataGet("https://pixiv.cat/$id.png"))!!.value.toInt()

            for (i in 1..num) {
                val `in`: InputStream = downloadImage("https://pixiv.cat/$id-$i.png")!!
                FileUtils.saveFileFromStream(`in`, File("${PluginConfig.database.SaveAddress}$id-$i.png"))
            }
        } finally {
            try {
                increaseEntry(id, 1, pJ.title, tags, pJ.userId.toLong(), pJ.userName, senderId)
            } catch (e: SQLIntegrityConstraintViolationException) {
                println("数据库已保存")
            }
            println("成功")
        }

        ImageInfo(id, num, pJ.title, tags, pJ.userId, pJ.userName)
    }
}

@KtorExperimentalAPI
suspend fun downloadPixivPage(id: Long): String {
    return try {
        HttpClient(OkHttp) {
            engine {
                proxy = ProxyBuilder.socks("127.0.0.1", PluginConfig.database.ProxyPort)
            }
        }.use { client -> client.get("https://www.pixiv.net/artworks/${id}") }
    } catch (e: Exception) {
        println(e)
        println("连接失败")
        ""
    }
}

