package com.xiaoyv404.mirai.service.ero.localGallery

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Pixiv
import com.xiaoyv404.mirai.databace.dao.gallery.GalleryTag
import com.xiaoyv404.mirai.databace.dao.gallery.GalleryTagMap
import com.xiaoyv404.mirai.databace.dao.gallery.save
import com.xiaoyv404.mirai.databace.dao.gallery.update
import com.xiaoyv404.mirai.service.ero.SQLInteraction
import com.xiaoyv404.mirai.service.tool.FileUtils
import com.xiaoyv404.mirai.service.tool.KtorUtils
import io.ktor.client.request.*
import kotlinx.serialization.ExperimentalSerializationApi
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
    suspend fun send(ii: Process.Img.Info) {
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
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun unformat(id: String, senderId: Long): Boolean {
        val formatInfo = try {
            KtorUtils.proxyClient.get<String>(
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
        } catch (_: Exception) {
            1
        }

        val fe = Process.Img.getSave(num, id)

        val info = Process.Img.Info(id.toLong(), num, pJ.title, tags, pJ.userId.toLong(), pJ.userName, fe)

        try {
            increaseEntry(info, senderId, pJ.tags.tags)
        } catch (_: SQLIntegrityConstraintViolationException) {
            PluginMain.logger.info("数据库已经保存pid: $id")
        }

        send(info)

        return false
    }

    object Process {
        fun linkInfo(ii: Img.Info): String = """
                作品ID: ${ii.id}
                标题: ${ii.title}
                标签: ${ii.tags}
                图片数: ${ii.picturesNum}
                作者名称: ${ii.userName}
                作者ID: ${ii.userId}
            """.trimIndent()
        object Img {
            data class Info(
                val id: Long?,
                val picturesNum: Int,
                val title: String?,
                val tags: String?,
                val userId: Long?,
                val userName: String?,
                val extension: String?,
            )
            suspend fun getSave(num: Int, id: String): String {
                var fe = ""
                if (num != 1) {
                    PluginMain.logger.info("含有$num 张图片")
                    for (i in 1..num) {
                        PluginMain.logger.info("正在保存第$i 张图片")
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
                    "image/png"  -> "png"
                    "image/jpeg" -> "jpg"
                    else         -> "???"
                }
                bSrc.reset()
                FileUtils.saveFileFromStream(bSrc, PluginMain.resolveDataFile("$dst.$fe"))
                return fe
            }
        }
    }
}

fun increaseEntry(
    da: LocalGallery.Process.Img.Info,
    creator: Long,
    tagsL: List<Tag>,
) {
    SQLInteraction.Gallerys.insert(da, creator)
    tagsL.forEach { tag ->
        val tagInfo = GalleryTag().findByTagName(tag.tag)
        if (tagInfo != null) {
            GalleryTag {
                tagid = tagInfo.tagid
                num = tagInfo.num + 1
            }.update()
        } else {
            val tagidA = GalleryTag {
                tagname = tag.tag
                num = 1
            }.save()
            GalleryTagMap {
                tagid = tagidA
                pid = da.id!!
            }.save()
        }
    }
}