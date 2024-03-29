package com.xiaoyv404.mirai.app.ero.localGallery

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.dao.*
import com.xiaoyv404.mirai.model.gallery.*
import com.xiaoyv404.mirai.tool.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.apache.tika.*
import java.io.*


suspend fun Gallery.send(msg: MessageEvent){
    if (this.picturesMun == 1) {
        msg.reply(
            PluginMain.resolveDataFile("gallery/${this.id}.${this.extension}")
                .uploadAsImage(msg.subject).plus(LocalGallerys.Process.linkInfo(this))
        )
    } else {
        msg.reply(
            buildForwardMessage(msg.subject) {
                msg.subject.bot.says(LocalGallerys.Process.linkInfo(this@send))
                for (i in 1..this@send.picturesMun) {
                    msg.subject.bot.says(
                        PluginMain.resolveDataFile("gallery/${this@send.id}-$i.${this@send.extension}")
                            .uploadAsImage(msg.subject)
                    )
                }
            }
        )
    }
}
class LocalGallerys(val msg: MessageEvent) {

    private val log = PluginMain.logger

    private val format = Json { ignoreUnknownKeys = true }


    /**
     *  获取并保存信息和图片到本地，并发送到[msg]
     *
     *  @return false 表示未报错, true 表示报错
     */
    @ExperimentalSerializationApi
    suspend fun unformat(idA: String, senderId: Long, outPut: Boolean): Boolean {
        val formatInfo = try {
            ClientUtils.get<String>(
                "https://www.pixiv.net/artworks/" +
                        idA
            )
        } catch (e: Exception) {
            log.error(e)
            return true
        }
        val num: Int =
            Regex("(?<=<p>這個作品ID中有只有 )\\d+(?= 張圖片，請指定正確的頁數，請參考<a href=\"https://pixiv.cat/\">首頁</a>說明。</p>)")
                .find(
                    ClientUtils.useHttpClient {
                        it.config {
                            expectSuccess = false
                        }.get("https://pixiv.re/$idA-114514.png").body<String>()
                    }
                )?.value?.toInt() ?: 1


        val fe = Process.Img.getSave(num, idA)

        format.decodeFromString<PixivJson>(
            Regex("(?=\\{\"illustId\":\").*?(?=,\"userIllusts\")")
                .find(formatInfo)!!.value + "}"
        ).let { pj ->
            var tagsA = ""
            pj.tags.tags.forEach {
                tagsA += it.tag + ","
            }
            tagsA.subSequence(0, tagsA.length - 1)

            val info = Gallery {
                id = idA.toLong()
                picturesMun = num
                title = pj.title
                tags = tagsA
                userId = pj.userId.toLong()
                userName = pj.userName
                creator = senderId
                extension = fe
            }

            increaseEntry(info, pj.tags.tags)

            if (!outPut) {
                info.send(msg)
            } else
                log.info("已关闭输出")
        }

        return false
    }

    object Process {

        private val log = PluginMain.logger

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
                    log.info("含有$num 张图片")
                    for (i in 1..num) {
                        log.info("正在保存第$i 张图片")
                        val `in` = ClientUtils.get<InputStream>("https://pixiv.re/$id-$i.png")
                        fe = verifyExtensionAndSaveFile(`in`, "gallery/$id-$i")
                    }
                } else {
                    log.info("含有1 张图片")
                    val `in` = ClientUtils.get<InputStream>("https://pixiv.re/$id.png")
                    fe = verifyExtensionAndSaveFile(`in`, "gallery/$id")
                }
                return fe
            }

            private fun verifyExtensionAndSaveFile(src: InputStream, dst: String): String {
                val bSrc = BufferedInputStream(src)
                bSrc.mark(0)
                var fe = Tika().detect(bSrc)
                log.info("文件格式: $fe")
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