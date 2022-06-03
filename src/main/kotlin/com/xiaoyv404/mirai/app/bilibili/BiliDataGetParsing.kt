package com.xiaoyv404.mirai.app.bilibili

import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.NfAppMessageHandler
import com.xiaoyv404.mirai.core.gid
import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.databace.dao.authorityIdentification
import com.xiaoyv404.mirai.databace.dao.isBot
import com.xiaoyv404.mirai.tool.KtorUtils
import com.xiaoyv404.mirai.tool.parsingVideoDataString
import io.ktor.client.request.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.InputStream

val format = Json { ignoreUnknownKeys = true }

@App
class BiliBiliVideoParse : NfAppMessageHandler() {
    override fun getAppName() = "BiliBiliVideoParse"
    override fun getVersion() = "1.0.2"
    override fun getAppDescription() = "b站视频解析"

    override suspend fun handleMessage(msg: MessageEvent) {
        if (msg.uid().isBot())
            return

        if (authorityIdentification(
                msg.uid(),
                msg.gid(),
                "BiliBiliParsing"
            )
        ) return

        val str = msg.message.contentToString()
        biliABvFind(str, msg)
    }
}

@OptIn(ExperimentalSerializationApi::class)
suspend fun biliABvFind(str: String, msg: MessageEvent) {
    Regex("BV1[1-9A-NP-Za-km-z]{9}").find(str)?.let {
        val bv = it.value
        if (authorityIdentification(
                msg.uid(), msg.gid(), "BiliBiliParsing"
            )
        ) return
        uJsonVideo(
            KtorUtils.normalClient.get(
                "https://api.bilibili.com/x/web-interface/view?bvid=$bv"
            ), msg.subject
        )

    }
    Regex("(av|AV)([1-9]\\d{0,18})").find(str)?.let {
        val av = it.groups[2]!!.value
        if (authorityIdentification(
                msg.uid(), msg.gid(), "BiliBiliParsing"
            )
        ) return
        uJsonVideo(
            KtorUtils.normalClient.get(
                "https://api.bilibili.com/x/web-interface/view?aid=$av"
            ), msg.subject
        )

    }
}

//用于格式化Json并发送
@ExperimentalSerializationApi
suspend fun uJsonVideo(uJsonVideo: String, group: Contact) {
    /**
     * 如果pJson中含有data字段时不会抛出[SerializationException]，不含有则反之
     * 当未抛出[SerializationException]异常时，正常执行，使用[VideoDataJson]格式化并发送
     * 当抛出[SerializationException]异常时，被catch抓住并使用[AbnormalVideoDataJson]格式化并发送
     */
    try {
        val pJson = format.decodeFromString<VideoDataJson>(uJsonVideo)
        group.sendMessage(
            KtorUtils.normalClient.get<InputStream>(pJson.data.pic).uploadAsImage(group)
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