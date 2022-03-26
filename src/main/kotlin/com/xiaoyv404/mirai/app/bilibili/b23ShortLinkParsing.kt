package com.xiaoyv404.mirai.app.bilibili

import com.xiaoyv404.mirai.databace.dao.authorityIdentification
import com.xiaoyv404.mirai.databace.Bilibili
import com.xiaoyv404.mirai.databace.dao.isNotBot
import com.xiaoyv404.mirai.tool.KtorUtils
import io.ktor.client.*
import io.ktor.client.request.*
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages


fun b23ShortLinkEntrance() {
    GlobalEventChannel.subscribeGroupMessages {
        finding(Bilibili.b23Find) {
            if (authorityIdentification(
                    sender.id,
                    group.id,
                    "BiliBiliParsing"
                ) && sender.isNotBot()
            ) {
                val b23 = it.value
                val b23Data = b23DataGet(b23)
                when {
                    Bilibili.biliBvFind.containsMatchIn(b23Data) -> {
                        val bv = Bilibili.biliBvFind.find(b23Data)!!.value
                        uJsonVideo(
                            KtorUtils.normalClient.get(
                                "https://api.bilibili.com/x/web-interface/view?bvid=$bv"
                            ), group
                        )
                    }
                }
            }
        }
    }
}

suspend fun b23DataGet(url: String): String {
    return HttpClient {
        followRedirects = false
        expectSuccess = false
    }.use { clien -> clien.get(url) }
}