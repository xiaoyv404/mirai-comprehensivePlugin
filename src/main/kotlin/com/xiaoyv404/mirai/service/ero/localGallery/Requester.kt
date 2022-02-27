package com.xiaoyv404.mirai.service.ero.localGallery

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Pixiv
import com.xiaoyv404.mirai.databace.dao.gallery.*
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

class LocalGallery(private val subject: Contact) {
    suspend fun send(ii: Gallery) {
        if (ii.picturesMun == 1) {
            subject.sendMessage(
                PluginMain.resolveDataFile("gallery/${ii.id}.${ii.extension}")
                    .uploadAsImage(subject).plus(Process.linkInfo(ii))
            )
        } else {
            subject.sendMessage(
                buildForwardMessage(subject) {
                    subject.bot.says(Process.linkInfo(ii))
                    for (i in 1..ii.picturesMun) {
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
    suspend fun unformat(idA: String, senderId: Long, outPut: Boolean): Boolean {
        val formatInfo = try {
            KtorUtils.normalClient.get<String>(
                "https://www.pixiv.net/artworks/" +
                    idA
            )
        } catch (e: Exception) {
            PluginMain.logger.error(e)
            return true
        }

        val pJ = format.decodeFromString<PixivJson>(
            Pixiv.worksInfoFind
                .find(formatInfo)!!.value + "}"
        )

        var tagsA = ""
        pJ.tags.tags.forEach {
            tagsA += it.tag + ","
        }
        tagsA.subSequence(0, tagsA.length - 1)

        val num: Int = try {
            Pixiv.worksNumberFind.find(KtorUtils.normalClient.config {
                expectSuccess = false
            }.get<String>("https://pixiv.re/$idA.png"))?.value?.toInt() ?: 1
        } catch (_: Exception) {
            1
        }

        val fe = Process.Img.getSave(num, idA)

        val info = Gallery {
            id = idA.toLong()
            picturesMun = num
            title = pJ.title
            tags = tagsA
            userId = pJ.userId.toLong()
            userName = pJ.userName
            creator = senderId
            extension = fe
        }

        increaseEntry(info, pJ.tags.tags)

        if (!outPut) {
            send(info)
        } else
            PluginMain.logger.info("已关闭输出")
        return false
    }

    object Process {
        fun linkInfo(ii: Gallery): String = """
                作品ID: ${ii.id}
                标题: ${ii.title}
                标签: ${ii.tags}
                图片数: ${ii.picturesMun}
                作者名称: ${ii.userName}
                作者ID: ${ii.userId}
            """.trimIndent()
        object Img {
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
    da: Gallery,
    tagsL: List<Tag>,
) {
    val saveS = da.save()
    if (saveS) {
        return
    }
    tagsL.forEach { tag ->
        val tagInfo = GalleryTag { tagname = tag.tag }.findByTagName()
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
                pid = da.id
            }.save()
        }
    }
}