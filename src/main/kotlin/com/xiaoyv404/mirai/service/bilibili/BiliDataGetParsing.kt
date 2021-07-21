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
            //���ڸ�ʽ��Json������
            suspend fun uJsonVideo(uJsonVideo: String) {
                /**
                 * ���pJson�к���data�ֶ�ʱ�����׳�[SerializationException]����������֮
                 * ��δ�׳�[SerializationException]�쳣ʱ������ִ�У�ʹ��[VideoDataJson]��ʽ��������
                 * ���׳�[SerializationException]�쳣ʱ����catchץס��ʹ��[AbnormalVideoDataJson]��ʽ��������
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
                        -404  -> group.sendMessage("��, ��Ƶ������Ŷ")
                        -400  -> group.sendMessage("404�ֳ�Bug��, ��ȥ���������ް�")
                        -403  -> group.sendMessage("404��Ȩ�޲���Ŷ")
                        62002 -> group.sendMessage("��Ƶ���ɼ���, ������ѻ��ָ���Щʲô��")
                    }
                }
            }
            //�����Ϣ���Ƿ���(Av|Bv)
            when {
                Bilibili.biliBvFind.containsMatchIn(message.contentToString()) -> {
                    //����Ƿ���BiliBili��������
                    when (groupDataRead(group.id)[0].biliStatus) {
                        1    -> {//���sender.id�Ƿ���ڻ�����id
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
                        else -> group.sendMessage("��, ����û����BiliBili��������Ŷ")
                    }
                }
                Bilibili.biliAvFind.containsMatchIn(it)                        -> {
                    //����Ƿ���BiliBili��������
                    when (groupDataRead(group.id)[0].biliStatus) {
                        1    -> {//���sender.id�Ƿ���ڻ�����id
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
                        else -> group.sendMessage("��, ����û����BiliBili��������Ŷ")
                    }
                }
            }
        }
    }
}

/**
 *����GET��Ƶ��Ϣ
 * id����Ƶ��ID(AV|BV)
 * mode�ǿ���GET��ģʽ��(aid|bvid)
 * PS:��Ϊmode��String���͵����룬���Զ������м��ߵ�Ҫ���������-400�����ȼ��һ�����
 **/
suspend fun videoDataGet(id: String, mode: String): String {
    return HttpClient().use { clien -> clien.get("https://api.bilibili.com/x/web-interface/view?$mode=$id") }
}
