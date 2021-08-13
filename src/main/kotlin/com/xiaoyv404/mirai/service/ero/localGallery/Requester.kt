package com.xiaoyv404.mirai.service.ero.localGallery

import com.xiaoyv404.mirai.PluginConfig
import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Pixiv
import com.xiaoyv404.mirai.service.ero.increaseEntry
import com.xiaoyv404.mirai.service.tool.FileUtils
import com.xiaoyv404.mirai.service.tool.KtorUtils
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.sql.SQLIntegrityConstraintViolationException

val format = Json { ignoreUnknownKeys = true }

@KtorExperimentalAPI
suspend fun unformat(id: String, senderId: Long): ImageInfo {
    val formatInfo: String
    try {
        formatInfo = KtorUtils.proxyClient.get(
            "https://www.pixiv.net/artworks/" +
                id
        )
    } catch (e: Exception) {
        PluginMain.logger.error(e)
        throw RuntimeException("服务器不理人了。。。")
    }

    val pJ = format.decodeFromString<PixivJson>(
        Pixiv.worksInfoFind
            .find(formatInfo)!!.value + "}"
    )

    var tags = ""
    pJ.tags.tags.forEach {
        tags += it.tag + ","
    }
    tags.subSequence(0, tags.length - 1)

    var num: Int
    try{
        num = Pixiv.worksNumberFind.find(KtorUtils.normalClient.config {
            expectSuccess = false
        }.get<String>("https://pixiv.cat/$id.png"))?.value?.toInt() ?: 0
    }catch (e: Exception){
        num = 0
    }

    if (num != 0) {
        println("含有$num 张图片")
        for (i in 1..num) {
            val `in` = KtorUtils.normalClient.get<InputStream>("https://pixiv.cat/$id-$i.png")
            FileUtils.saveFileFromStream(`in`, File("${PluginConfig.database.SaveAddress}$id-$i.png"))
        }
    } else {
        println("含有1 张图片")
        num = try {
            val `in` = KtorUtils.normalClient.get<InputStream>("https://pixiv.cat/$id.png")
            FileUtils.saveFileFromStream(`in`, File("${PluginConfig.database.SaveAddress}$id.png"))
            1
        } catch (e: Exception) {
            println(e)
            0
        }
    }
    try {
        increaseEntry(id.toLong(), num, pJ.title, tags, pJ.userId.toLong(), pJ.userName, senderId, pJ.tags.tags)
    } catch (e: SQLIntegrityConstraintViolationException) {
        PluginMain.logger.info("数据库已经保存pid: $id")
    }

    return ImageInfo(id.toLong(), num, pJ.title, tags, pJ.userId.toLong(), pJ.userName)
}
