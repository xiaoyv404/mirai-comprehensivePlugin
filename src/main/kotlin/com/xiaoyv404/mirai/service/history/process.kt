package com.xiaoyv404.mirai.service.history

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.dao.itNotBot
import com.xiaoyv404.mirai.service.tool.FileUtils.saveFileFromString
import kotlinx.serialization.SerializationException
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageChain.Companion.serializeToJsonString

fun historyEntrance() {
    GlobalEventChannel.subscribeGroupMessages {
        always {
            if (sender.itNotBot()) {
                val msg =  when (message[1]) {
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
                    else -> {
                        return@always
                    }
                }
                saveFileFromString(
                    msg + "\n",
                    PluginMain.resolveDataFile("history/${group.id}.txt")
                )
            }
            try {
                insertMessage(
                    message.time,
                    "msg",
                    group.id,
                    group.name,
                    sender.id,
                    sender.nameCard,
                    message.ids[0].toLong(),
                    message.serializeToJsonString()
                )
            } catch (_: SerializationException) {
            }
        }
    }
}