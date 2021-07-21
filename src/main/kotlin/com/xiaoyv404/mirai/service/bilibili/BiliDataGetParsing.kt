package com.xiaoyv404.mirai.service.bilibili

import com.xiaoyv404.mirai.databace.Bilibili
import com.xiaoyv404.mirai.service.getUserInformation
import com.xiaoyv404.mirai.service.groupDataRead
import com.xiaoyv404.mirai.service.tool.downloadImage
import com.xiaoyv404.mirai.service.tool.parsingVideoDataString
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage

val format = Json { ignoreUnknownKeys = true }

fun biliVideoEntrance() {
    GlobalEventChannel.subscribeGroupMessages {
        always {
            //用于格式化Json并发送
            suspend fun uJsonVideo(uJsonVideo: String) {
                /**
                 * 如果pJson中含有data字段时不会抛出[SerializationException]，不含有则反之
                 * 当未抛出[SerializationException]异常时，正常执行，使用[VideoDataJson]格式化并发送
                 * 当抛出[SerializationException]异常时，被catch抓住并使用[AbnormalVideoDataJson]格式化并发送
                 */
                try {
                    val pJson = format.decodeFromString<VideoDataJson>(uJsonVideo)
                    group.sendMessage(
                        downloadImage(pJson.data.pic)!!
                            .uploadAsImage(group, "png")
                            .plus(parsingVideoDataString(pJson))
                    )
                } catch (e: SerializationException) {
                    val pJson = format.decodeFromString<AbnormalVideoDataJson>(uJsonVideo)
                    when (pJson.code) {
                        -404  -> group.sendMessage("喵, 视频不存在哦")
                        -400  -> group.sendMessage("404又出Bug惹, 快去叫主人来修叭")
                        -403  -> group.sendMessage("404的权限不足哦")
                        62002 -> group.sendMessage("视频不可见惹, 这个死已婚又干了些什么啊")
                    }
                }
            }
            //检测消息中是否含有(Av|Bv)
            when {
                Bilibili.biliBvFind.containsMatchIn(message.contentToString()) -> {
                    //检测是否开启BiliBili解析功能
                    when (groupDataRead(group.id)[0].biliStatus) {
                        1    -> {//检测sender.id是否等于机器人id
                            if (getUserInformation(sender.id).bot != true) {
                                uJsonVideo(
                                    videoDataGet(
                                        Bilibili.biliBvFind.find(it)!!
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
                Bilibili.biliAvFind.containsMatchIn(it)                        -> {
                    //检测是否开启BiliBili解析功能
                    when (groupDataRead(group.id)[0].biliStatus) {
                        1    -> {//检测sender.id是否等于机器人id
                            if (getUserInformation(sender.id).bot != true) {
                                uJsonVideo(
                                    videoDataGet(
                                        Bilibili.biliAvFind.find(message.contentToString())!!
                                            .value
                                            .replace(Regex("(av|AV)"), ""),
                                        "aid"
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
    }
}

/**
 *用于GET视频信息
 * id是视频的ID(AV|BV)
 * mode是控制GET的模式的(aid|bvid)
 * PS:因为mode是String类型的输入，所以对输入有极高的要求，如果出现-400可以先检查一下这个
 **/
suspend fun videoDataGet(id: String, mode: String): String {
    return HttpClient().use { clien -> clien.get("https://api.bilibili.com/x/web-interface/view?$mode=$id") }
}
