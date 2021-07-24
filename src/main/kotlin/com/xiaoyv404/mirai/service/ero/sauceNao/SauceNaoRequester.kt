package com.xiaoyv404.mirai.service.ero.sauceNao

import com.xiaoyv404.mirai.PluginConfig
import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.service.tool.KtorUtils
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.InputStream
import java.net.URLDecoder

@KtorExperimentalAPI
@Suppress("BlockingMethodInNonBlockingContext")
class SauceNaoRequester(private val subject: Contact) {
    private val jsonBuild = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    private var result: SauceNaoResponse.Result? = null

    suspend fun search(image: Image) {
        try {
            val json: String =
                KtorUtils.normalClient.get(
                    "https://saucenao.com/search.php?" +
                        "output_type=2&" +
                        "api_key=${PluginConfig.database.sauceNaoApiKey}&" +
                        "db=5&" +
                        "numres=1&" +
                        "url=${URLDecoder.decode(image.queryUrl(), "utf-8")}"
                )
            PluginMain.logger.info(json)
            parseJson(json)
        } catch (e: Exception) {
            subject.sendMessage(
                "出现错误＞﹏＜请阁下到控制台查看\n" + e.message?.replace(
                    PluginConfig.database.sauceNaoApiKey,
                    "/$/{APIKEY/}"
                )
            )
            PluginMain.logger.error(e)
            throw e
        }
    }

    private fun parseJson(json: String) {
        val res: SauceNaoResponse = jsonBuild.decodeFromString(json)
        result = res.results[0]
    }

    suspend fun sendResult(message: MessageChain) {
        val image = KtorUtils.normalClient.get<InputStream>(result!!.header.thumbnail).uploadAsImage(subject)
        val msg = when (result!!.header.index_id) {
            // Index #5: Pixiv Images
            5    -> {
                "来源：Pixiv Images\n" +
                    "题目：${result!!.data.title}\n" +
                    "相似度：${result!!.header.similarity}\n" +
                    "pixiv id：${result!!.data.pixiv_id}\n" +
                    "作者：${result!!.data.member_name}\n" +
                    "作者id：${result!!.data.member_id}\n" +
                    "源链接：${result!!.data.ext_urls}"
            }
            // Index #21: Anime
            21   -> {
                "来源：Anime\n" +
                    "动画名：${result!!.data.source}\n" +
                    "相似度：${result!!.header.similarity}\n" +
                    "anidb_id：${result!!.data.pixiv_id}\n" +
                    "年代：${result!!.data.year}\n" +
                    "集数：${result!!.data.part}\n" +
                    "源链接：${result!!.data.ext_urls}"
            }
            // Index #34: deviantArt
            34   -> {
                "来源：deviantArt\n" +
                    "题目：${result!!.data.title}\n" +
                    "相似度：${result!!.header.similarity}\n" +
                    "图片id：${result!!.data.da_id}\n" +
                    "作者：${result!!.data.author_name}\n" +
                    "作者链接：${result!!.data.author_url}\n" +
                    "源链接：${result!!.data.ext_urls}"
            }
            40   -> {
                "来源：FurAffinity\n"
            }
            else -> "暂时无法解析的参数, 数据库：${result!!.header.index_name}\n 请把开发者揪出来给他看看结果"
        }
        subject.sendMessage(message.quote() + PlainText(msg) + image)
    }
}