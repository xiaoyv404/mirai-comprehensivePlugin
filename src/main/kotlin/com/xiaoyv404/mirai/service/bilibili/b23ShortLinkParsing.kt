package com.xiaoyv404.mirai.service.bilibili

import com.xiaoyv404.mirai.databace.Bilibili
import com.xiaoyv404.mirai.service.getUserInformation
import com.xiaoyv404.mirai.service.authorityIdentification
import com.xiaoyv404.mirai.service.tool.KtorUtils
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.util.*
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages


@KtorExperimentalAPI
fun b23ShortLinkEntrance() {
    GlobalEventChannel.subscribeGroupMessages {
        finding(Bilibili.b23Find) {
            if ((authorityIdentification(
                    sender.id,
                    group.id,
                    "BiliBiliParsing"
                )) && (getUserInformation(sender.id).bot != true)
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