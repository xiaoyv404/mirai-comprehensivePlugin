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
     *  ��ȡ��������Ϣ��ͼƬ�����أ������͵�[subject]
     *
     *  @return false ��ʾδ����, true ��ʾ����
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
            PluginMain.logger.info("���ݿ��Ѿ�����pid: $id")
        }

        send(info)

        return false
    }

    object Process {
        fun linkInfo(ii: ImageInfo): String = """
                ��ƷID: ${ii.id}
                ����: ${ii.title}
                ��ǩ: ${ii.tags}
                ͼƬ��: ${ii.picturesNum}
                ��������: ${ii.userName}
                ����ID: ${ii.userId}
            """.trimIndent()
        object Img {
            @KtorExperimentalAPI
            suspend fun getSave(num: Int, id: String): String {
                var fe = ""
                if (num != 1) {
                    PluginMain.logger.info("����$num ��ͼƬ")
                    for (i in 1..num) {
                        val `in` = KtorUtils.normalClient.get<InputStream>("https://pixiv.re/$id-$i.png")
                        fe = verifyExtensionAndSaveFile(`in`, "gallery/$id-$i")
                    }
                } else {
                    PluginMain.logger.info("����1 ��ͼƬ")
                    val `in` = KtorUtils.normalClient.get<InputStream>("https://pixiv.re/$id.png")
                    fe = verifyExtensionAndSaveFile(`in`, "gallery/$id")
                }
                return fe
            }

            private fun verifyExtensionAndSaveFile(src: InputStream, dst: String): String {
                val bSrc = BufferedInputStream(src)
                bSrc.mark(0)
                var fe = Tika().detect(bSrc)
                PluginMain.logger.info("�ļ���ʽ: $fe")
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