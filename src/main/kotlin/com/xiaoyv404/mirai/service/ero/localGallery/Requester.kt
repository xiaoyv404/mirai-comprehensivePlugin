package com.xiaoyv404.mirai.service.ero.localGallery

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Pixiv
import com.xiaoyv404.mirai.service.ero.increaseEntry
import com.xiaoyv404.mirai.service.tool.FileUtils
import com.xiaoyv404.mirai.service.tool.KtorUtils
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.tika.Tika
import java.io.BufferedInputStream
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

    val num: Int = try {
        Pixiv.worksNumberFind.find(KtorUtils.normalClient.config {
            expectSuccess = false
        }.get<String>("https://pixiv.re/$id.png"))?.value?.toInt() ?: 1
    } catch (e: Exception) {
        1
    }

    var fe = ""
    if (num != 1) {
        PluginMain.logger.info("含有$num 张图片")
        for (i in 1..num) {
            val `in` = KtorUtils.normalClient.get<InputStream>("https://pixiv.re/$id-$i.png")
            fe = verifyExtensionAndSaveFile(`in`, "gallery/$id-$i")
        }
    } else {
        PluginMain.logger.info("含有1 张图片")
        val `in` = KtorUtils.normalClient.get<InputStream>("https://pixiv.re/$id.png")
        fe = verifyExtensionAndSaveFile(`in`, "gallery/$id")
    }

    try {
        increaseEntry(id.toLong(), num, pJ.title, tags, pJ.userId.toLong(), pJ.userName, senderId, pJ.tags.tags, fe)
    } catch (e: SQLIntegrityConstraintViolationException) {
        PluginMain.logger.info("数据库已经保存pid: $id")
    }

    return ImageInfo(id.toLong(), num, pJ.title, tags, pJ.userId.toLong(), pJ.userName, fe)
}

fun verifyExtensionAndSaveFile(src: InputStream, dst: String): String {
    val bSrc = BufferedInputStream(src)
    bSrc.mark(0)
    var fe = Tika().detect(bSrc)
    PluginMain.logger.info("文件格式: $fe")
    fe = when (fe) {
        "image/png"  -> "png"
        "image/jpeg" -> "jpg"
        else         -> "???"
    }
    bSrc.reset()
    FileUtils.saveFileFromStream(bSrc,  PluginMain.resolveDataFile("$dst.$fe"))
    return fe
}