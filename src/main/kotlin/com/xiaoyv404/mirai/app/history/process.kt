package com.xiaoyv404.mirai.app.history

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.databace.dao.HistoryRecord
import com.xiaoyv404.mirai.databace.dao.save
import com.xiaoyv404.mirai.tool.FileUtils
import kotlinx.serialization.SerializationException
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageChain.Companion.serializeToJsonString
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@App
class History : NfApp(){
    override fun getAppName() = "History"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "ÏûÏ¢¼ÇÂ¼Æ÷"
    override fun init() {
        GlobalEventChannel.subscribeGroupMessages {
            always {
                val msg = when (message[1]) {
                    is PlainText      -> {
                        message.content
                    }
                    is Image          -> {
                        message.content
                    }
                    is At             -> {
                        message.content
                    }
                    is AtAll          -> {
                            message.content
                        }
                        is Face       -> {
                            message.content
                        }
                        is MarketFace -> {
                            message.content
                        }
                        else          -> {
                            return@always
                        }
                    }
                    FileUtils.saveFileFromString(
                        msg + "\n",
                        PluginMain.resolveDataFile("history/${group.id}.txt")
                    )

                try {
                    HistoryRecord {
                        sendTime = LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(message.time.toLong() + 10800), ZoneId.ofOffset(
                                "UTC",
                                ZoneOffset.of("+18")
                            )
                        )
                        eventType = "msg"
                        groupId = group.id
                        groupName = group.name
                        senderId = sender.id
                        senderName = sender.nameCard
                        msgId = message.ids[0].toLong()
                        content = message.serializeToJsonString()
                    }.save()
                } catch (_: SerializationException) {
                }
            }
        }

    }
}