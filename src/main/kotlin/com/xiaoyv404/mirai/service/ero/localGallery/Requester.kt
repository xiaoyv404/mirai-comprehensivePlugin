package com.xiaoyv404.mirai.service.ero.localGallery

import com.xiaoyv404.mirai.PluginConfig
import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Pixiv
import com.xiaoyv404.mirai.service.ero.increaseEntry
import com.xiaoyv404.mirai.service.tool.FileUtils
import com.xiaoyv404.mirai.service.tool.KtorUtils
import com.xiaoyv404.mirai.service.tool.dataGet
import com.xiaoyv404.mirai.service.tool.downloadImage
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
    var num = 1
    val formatInfo: String
    try {
        formatInfo = KtorUtils.proxyClient.get(
            "https://www.pixiv.net/artworks/" +
                id
        )
    } catch (e: Exception) {
        PluginMain.logger.error(e)
        throw RuntimeException("�������������ˡ�����")
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
    try {
        val `in`: InputStream = downloadImage("https://pixiv.cat/$id.png")!!

        FileUtils.saveFileFromStream(`in`, File("${PluginConfig.database.SaveAddress}$id.png"))
    } catch (e: Exception) {
        num = Pixiv.worksNumberFind.find(dataGet("https://pixiv.cat/$id.png"))!!.value.toInt()

        for (i in 1..num) {
            val `in`: InputStream = downloadImage("https://pixiv.cat/$id-$i.png")!!
            FileUtils.saveFileFromStream(`in`, File("${PluginConfig.database.SaveAddress}$id-$i.png"))
        }
    } finally {
        try {
            increaseEntry(id.toLong(), 1, pJ.title, tags, pJ.userId.toLong(), pJ.userName, senderId, pJ.tags.tags)
        } catch (e: SQLIntegrityConstraintViolationException) {
        }
    }

    return ImageInfo(id.toLong(), num, pJ.title, tags, pJ.userId.toLong(), pJ.userName)
}
