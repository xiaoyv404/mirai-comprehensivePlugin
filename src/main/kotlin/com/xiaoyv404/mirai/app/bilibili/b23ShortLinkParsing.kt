package com.xiaoyv404.mirai.app.bilibili

import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.NfAppMessageHandler
import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.databace.dao.authorityIdentification
import com.xiaoyv404.mirai.databace.dao.isBot
import io.ktor.client.*
import io.ktor.client.request.*
import net.mamoe.mirai.event.events.MessageEvent

@App
class B23ShortLinkParse : NfAppMessageHandler() {
    override fun getAppName() = "b23ShortLinkParse"
    override fun getVersion() = "1.0.2"
    override fun getAppDescription() = "b23短链解析"

    override suspend fun handleMessage(msg: MessageEvent) {
        if (msg.uid().isBot())
            return

        Regex("(https?://b23.tv/\\S{6})").find(msg.message.contentToString())?.let {
            if (msg.authorityIdentification("BiliBiliParsing"))
                return

            val b23 = it.value
            val b23Data = b23DataGet(b23)
            biliABvFind(b23Data, msg)
        }
    }

    private suspend fun b23DataGet(url: String): String {
        return HttpClient {
            followRedirects = false
            expectSuccess = false
        }.use { clien -> clien.get(url) }
    }
}


