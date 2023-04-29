package com.xiaoyv404.mirai.app.history

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.dao.*
import com.xiaoyv404.mirai.model.*
import com.xiaoyv404.mirai.tool.*
import kotlinx.serialization.*
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageChain.Companion.serializeToJsonString
import java.time.*

@App
class History : NfAppMessageHandler() {
    override fun getAppName() = "History"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "消息记录器"
    override suspend fun handleMessage(msg: MessageEvent) {
        val message = msg.message
        val group = msg.subject
        if (group !is Group)
            return
        when (message[1]) {
            is PlainText  -> {
                message.content
            }
            is Image      -> {
                message.content
            }
            is At         -> {
                message.content
            }
            is AtAll      -> {
                message.content
            }
            is Face       -> {
                message.content
            }
            is MarketFace -> {
                message.content
            }
            else          -> {
                return
            }
        }.let {
            FileUtils.saveFileFromString(
                it + "\n",
                PluginMain.resolveDataFile("history/${msg.gid()}.txt")
            )
        }
        try {
            HistoryRecord {
                sendTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(message.time.toLong() + 10800), ZoneId.ofOffset(
                        "UTC",
                        ZoneOffset.of("+18")
                    )
                )
                eventType = "msg"
                groupId = msg.gid()
                groupName = group.name
                senderId = msg.uid()
                senderName = msg.senderName
                msgId = message.ids[0].toLong()
                content = message.serializeToJsonString()
            }.save()
        } catch (_: SerializationException) {
            // 不必理会
        }
    }
}