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
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.apache.tika.Tika
import java.io.BufferedInputStream
import java.io.InputStream
import java.sql.SQLIntegrityConstraintViolationException

class LocalGallery(private val subject: Contact) {
    suspend fun send(ii: ImageInfo) {
        if (ii.picturesNum == 1) {
            subject.sendMessage(
                PluginMain.resolveDataFile("gallery/${ii.id}.${ii.extension}")
                    .uploadAsImage(subject).plus(Process.linkInfo(ii))
            )
        } else {
            subject.sendMessage(
                buildForwardMessage(subject) {
                    subject.bot.says(Process.linkInfo(ii))
                    for (i in 1..ii.picturesNum) {
                        subject.bot.says(
                            PluginMain.resolveDataFile("gallery/${ii.id}-$i.${ii.extension}")
                                .uploadAsImage(subject)
                        )
                    }
                }
            )
        }
    }

    private val format = Json { ignoreUnknownKeys = true }


    /**
     *  获取并保存信息和图片到本地，并发送到[subject]
     *
     *  @return false 表示未报错, true 表示报错
     */
    @KtorExperimentalAPI
    suspend fun unformat(id: String, senderId: Long): Boolean {
        val formatInfo: String
        try {
            formatInfo = KtorUtils.proxyClient.get(
                "https://www.pixiv.net/artworks/" +
                    id
            )
        } catch (e: Exception) {
            PluginMain.logger.error(e)
            return true
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

        val fe = Process.Img.getSave(num, id)

        val info = ImageInfo(id.toLong(), num, pJ.title, tags, pJ.userId.toLong(), pJ.userName, fe)

        try {
            increaseEntry(info, senderId, pJ.tags.tags)
        } catch (e: SQLIntegrityConstraintViolationException) {
            PluginMain.logger.info("数据库已经保存pid: $id")
        }

        send(info)

        return false
    }

    object Process {
        fun linkInfo(ii: ImageInfo): String = """
                作品ID: ${ii.id}
                标题: ${ii.title}
                标签: ${ii.tags}
                图片数: ${ii.picturesNum}
                作者名称: ${ii.userName}
                作者ID: ${ii.userId}
            """.trimIndent()
        object Img {
            @KtorExperimentalAPI
            suspend fun getSave(num: Int, id: String): String {
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
                return fe
            }

            private fun verifyExtensionAndSaveFile(src: InputStream, dst: String): String {
                val bSrc = BufferedInputStream(src)
                bSrc.mark(0)
                var fe = Tika().detect(bSrc)
                PluginMain.logger.info("文件格式: $fe")
                fe = when (fe) {
                    "image/png" -> "png"
                    "image/jpeg" -> "jpg"
                    else -> "???"
                }
                bSrc.reset()
                FileUtils.saveFileFromStream(bSrc, PluginMain.resolveDataFile("$dst.$fe"))
                return fe
            }
        }
    }
}