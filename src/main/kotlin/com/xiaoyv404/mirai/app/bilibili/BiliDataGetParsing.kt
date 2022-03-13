package com.xiaoyv404.mirai.app.bilibili

import com.xiaoyv404.mirai.app.accessControl.authorityIdentification
import com.xiaoyv404.mirai.databace.Bilibili
import com.xiaoyv404.mirai.databace.dao.isNotBot
import com.xiaoyv404.mirai.tool.KtorUtils
import com.xiaoyv404.mirai.tool.parsingVideoDataString
import io.ktor.client.request.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.InputStream

val format = Json { ignoreUnknownKeys = true }

fun biliVideoEntrance() {
    GlobalEventChannel.subscribeGroupMessages {
        finding(Bilibili.biliBvFind){
            val bv = it.value
            if ((authorityIdentification(
                    sender.id,
                    group.id,
                    "BiliBiliParsing"
                )) && sender.isNotBot()
            ) {
                uJsonVideo(
                    KtorUtils.normalClient.get(
                        "https://api.bilibili.com/x/web-interface/view?bvid=$bv"
                    ),group
                )
            }
        }
        finding(Bilibili.biliAvFind) {
            val av = it.groups[2]!!.value
            if ((authorityIdentification(
                    sender.id,
                    group.id,
                    "BiliBiliParsing"
                )) && sender.isNotBot()
            ) {
                uJsonVideo(
                    KtorUtils.normalClient.get(
                        "https://api.bilibili.com/x/web-interface/view?aid=$av"
                    ),group
                )
            }
        }
    }
}

//���ڸ�ʽ��Json������
@OptIn(ExperimentalSerializationApi::class)
suspend fun uJsonVideo(uJsonVideo: String, group: Contact) {
    /**
     * ���pJson�к���data�ֶ�ʱ�����׳�[SerializationException]����������֮
     * ��δ�׳�[SerializationException]�쳣ʱ������ִ�У�ʹ��[VideoDataJson]��ʽ��������
     * ���׳�[SerializationException]�쳣ʱ����catchץס��ʹ��[AbnormalVideoDataJson]��ʽ��������
     */
    try {
        val pJson = format.decodeFromString<VideoDataJson>(uJsonVideo)
        group.sendMessage(
            KtorUtils.normalClient.get<InputStream>(pJson.data.pic)
                .uploadAsImage(group)
                .plus(parsingVideoDataString(pJson))
        )
    } catch (e: SerializationException) {
        val pJson = format.decodeFromString<AbnormalVideoDataJson>(uJsonVideo)
        when (pJson.code) {
            -404  -> group.sendMessage("��, ��Ƶ������Ŷ")
            -400  -> group.sendMessage("404�ֳ�Bug��, ��ȥ���������ް�")
            -403  -> group.sendMessage("404��Ȩ�޲���Ŷ")
            62002 -> group.sendMessage("��Ƶ���ɼ���, ������ѻ��ָ���Щʲô��")
        }
    }
}