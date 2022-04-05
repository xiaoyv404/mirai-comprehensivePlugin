package com.xiaoyv404.mirai.app.ero.sauceNao

import com.xiaoyv404.mirai.PluginConfig
import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.tool.KtorUtils
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.InputStream
import java.net.URLDecoder

class SauceNaoRequester(private val msg: MessageEvent) {
    private val jsonBuild = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    private var result: SauceNaoResponse.Result? = null

    private val log = PluginMain.logger

    suspend fun search(image: Image) {
        try {
            val json: String =
                KtorUtils.normalClient.get(
                    "https://saucenao.com/search.php?" +
                        "output_type=2&" +
                        "api_key=${PluginConfig.database.sauceNaoApiKey}&" +
                        "db=5&" +
                        "numres=1&" +
                        "url=${
                            withContext(Dispatchers.IO) {
                                URLDecoder.decode(image.queryUrl(), "utf-8")
                            }
                        }"
                )
            log.info(json)
            parseJson(json)
        } catch (e: Exception) {
            msg.reply(
                "���ִ��󣾩n������µ�����̨�鿴\n" + e.message?.replace(
                    PluginConfig.database.sauceNaoApiKey,
                    "/$/{APIKEY/}"
                ), true
            )
            log.error(e)
            throw e
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun parseJson(json: String) {
        val res: SauceNaoResponse = jsonBuild.decodeFromString(json)
        result = res.results[0]
    }

    suspend fun sendResult() {
        val image = KtorUtils.normalClient.get<InputStream>(result!!.header.thumbnail).uploadAsImage(msg.subject)
        if (result!!.header.similarity.toFloat() < 60){
            msg.reply("�Ҳ�����ƥ���ֻ��${result!!.header.similarity}", true)
            return
        }
        val message = when (result!!.header.index_id) {
            // Index #5: Pixiv Images
            5    -> {
                "��Դ��Pixiv Images\n" +
                    "��Ŀ��${result!!.data.title}\n" +
                    "���ƶȣ�${result!!.header.similarity}\n" +
                    "pixiv id��${result!!.data.pixiv_id}\n" +
                    "���ߣ�${result!!.data.member_name}\n" +
                    "����id��${result!!.data.member_id}\n" +
                    "Դ���ӣ�${result!!.data.ext_urls}"
            }
            // Index #21: Anime
            21   -> {
                "��Դ��Anime\n" +
                    "��������${result!!.data.source}\n" +
                    "���ƶȣ�${result!!.header.similarity}\n" +
                    "anidb_id��${result!!.data.pixiv_id}\n" +
                    "�����${result!!.data.year}\n" +
                    "������${result!!.data.part}\n" +
                    "Դ���ӣ�${result!!.data.ext_urls}"
            }
            // Index #34: deviantArt
            34   -> {
                "��Դ��deviantArt\n" +
                    "��Ŀ��${result!!.data.title}\n" +
                    "���ƶȣ�${result!!.header.similarity}\n" +
                    "ͼƬid��${result!!.data.da_id}\n" +
                    "���ߣ�${result!!.data.author_name}\n" +
                    "�������ӣ�${result!!.data.author_url}\n" +
                    "Դ���ӣ�${result!!.data.ext_urls}"
            }
            40   -> {
                "��Դ��FurAffinity\n"
            }
            else -> "��ʱ�޷������Ĳ���, ���ݿ⣺${result!!.header.index_name}\n ��ѿ����߾����������������"
        }
        msg.reply(PlainText(message) + image, true)
    }
}