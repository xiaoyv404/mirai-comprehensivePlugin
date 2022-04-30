package com.xiaoyv404.mirai.app.bilibili

import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.NfAppMessageHandler
import com.xiaoyv404.mirai.core.gid
import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.databace.Bilibili
import com.xiaoyv404.mirai.databace.dao.authorityIdentification
import com.xiaoyv404.mirai.tool.KtorUtils
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.event.events.MessageEvent

@App
class B23ShortLinkParse : NfAppMessageHandler() {
    override fun getAppName() = "b23ShortLinkParse"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "b23短链解析"

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun handleMessage(msg: MessageEvent) {
        Bilibili.b23Find.find(msg.message.contentToString())?.let {
            if (authorityIdentification(
                    msg.uid(),
                    msg.gid(),
                    "BiliBiliParsing"
                )
            ) {
                val b23 = it.value
                val b23Data = b23DataGet(b23)
                when {
                    Bilibili.biliBvFind.containsMatchIn(b23Data) -> {
                        val bv = Bilibili.biliBvFind.find(b23Data)!!.value
                        uJsonVideo(
                            KtorUtils.normalClient.get(
                                "https://api.bilibili.com/x/web-interface/view?bvid=$bv"
                            ), msg.subject
                        )
                    }
                }
            }
        }
    }

    private suspend fun b23DataGet(url: String): String {
        return HttpClient {
            followRedirects = false
            expectSuccess = false
        }.use { clien -> clien.get(url) }
    }
}


