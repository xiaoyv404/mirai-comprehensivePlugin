package com.xiaoyv404.mirai.app.bilibili

import com.xiaoyv404.mirai.app.accessControl.authorityIdentification
import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.databace.dao.isNotBot
import com.xiaoyv404.mirai.tool.KtorUtils
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.InputStream

@OptIn(ExperimentalSerializationApi::class)
fun informationEntrance() {
    GlobalEventChannel.subscribeGroupMessages {
        finding(Command.getBiliBiliUpInformation) {
            if ((authorityIdentification(
                    sender.id,
                    group.id,
                    "BiliBiliParsing"
                )) && sender.isNotBot()
            ) {
                val rd = it.groups
                val uid = when {
                    (rd[4]?.value == null)                               -> {
                        "437952226"
                    }
                    (rd[5]!!.value == "-h" || rd[5]!!.value == "--help") -> {
                        group.sendMessage("help")
                        return@finding
                    }
                    else                                                 -> {
                        rd[5]!!.value
                    }
                }
                try {
                    val pD = format.decodeFromString<UserData>(statDataGet(uid)).data
                    val pUD = format.decodeFromString<UpInformation>(upDataGet(uid)).data
                    group.sendMessage(
                        KtorUtils.normalClient.get<InputStream>(pD.card.face)
                            .uploadAsImage(group)
                            .plus(
                                "${pD.card.name}($uid)\n" +
                                    "粉丝数: ${pD.follower}   关注数: ${pD.card.friend}   稿件数: ${pD.archive_count}\n" +
                                    "视频播放量: ${pUD.archive.view}    专栏阅读量: ${pUD.article.view}   获赞次数: ${pUD.likes}"
                            )
                    )
                } catch (e: Exception) {
                    group.sendMessage("他消失在了浩荡的宇宙中")
                }
            }
        }
    }
}


suspend fun upDataGet(mid: String): String {
    return HttpClient().use { clien ->
        clien.get("https://api.bilibili.com/x/space/upstat?mid=$mid") {
            header("Cookie", "SESSDATA=db7902be%2C1635745810%2C4a122%2A51")
        }
    }
}

suspend fun statDataGet(mid: String): String {
    return HttpClient().use { clien ->
        clien.get("https://api.bilibili.com/x/web-interface/card?mid=$mid")
    }
}

@Serializable
data class UpInformation(
    val code: Int,
    val `data`: Data2,
)

@Serializable
data class Data2(
    val archive: Archive,
    val article: Article,
    val likes: Int
)

@Serializable
data class Archive(
    val view: Int
)

@Serializable
data class Article(
    val view: Int
)

@Serializable
data class UserData(
    val code: Int,
    val `data`: Data3,
)

@Serializable
data class Data3(
    val archive_count: Int,
    val card: Card,
    val follower: Int,
)

@Serializable
data class Card(
    val attention: Int,
    val face: String,
    val friend: Int,
    val name: String,
)