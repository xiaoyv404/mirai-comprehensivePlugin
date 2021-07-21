package com.xiaoyv404.mirai.service.bilibili

import com.xiaoyv404.mirai.databace.Bilibili
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
        finding(Bilibili.b23Find).invoke {
            suspend fun uJsonVideo(uJsonVideo: String) {
                val pJson = format.decodeFromString<VideoDataJson>(uJsonVideo)
                group.sendMessage(
                    downloadImage(pJson.data.pic)!!
                        .uploadAsImage(group, "jpg")
                        .plus(parsingVideoDataString(pJson))
                )
            }
            when (groupDataRead(group.id)[0].biliStatus) {
                1    -> {
                    val b23Data = b23DataGet(Bilibili.b23Find.find(it)!!.value)
                    when {
                        Bilibili.biliBvFind.containsMatchIn(b23Data) -> uJsonVideo(
                            videoDataGet(
                                Bilibili.biliBvFind.find(b23Data)!!
                                    .value,
                                "bvid"
                            )
                        )
                    }
                }
                -1   -> {
                }
                else -> group.sendMessage("喵, 好像没开启BiliBili解析功能哦")
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