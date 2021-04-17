package com.xiaoyv404.mirai.service.bilibili

import com.xiaoyv404.mirai.service.groupDataRead
import com.xiaoyv404.mirai.service.tool.downloadImage
import com.xiaoyv404.mirai.service.tool.parsingVideoDataString
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.decodeFromString
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage


fun b23ShortLinkEntrance() {
    GlobalEventChannel.subscribeGroupMessages {
        matching(regular.b23Find).invoke {
            suspend fun uJsonVideo(uJsonVideo: String) {
                val pJson = format.decodeFromString<VideoDataJson>(uJsonVideo)
                group.sendMessage(
                    downloadImage(pJson.data.pic)!!
                        .uploadAsImage(group, "jpg")
                        .plus(parsingVideoDataString(pJson))
                )
            }
            if (groupDataRead(group.id) == true) {
                val b23Data = b23DataGet(regular.b23Find.find(message.contentToString())!!.value)
                when {
                    regular.biliBvFind.containsMatchIn(b23Data) -> uJsonVideo(
                        videoDataGet(
                            regular.biliBvFind.find(b23Data)!!
                                .value,
                            "bvid"
                        )
                    )
                }
            } else {
                group.sendMessage("喵, 好像没开启BiliBili解析功能哦")
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