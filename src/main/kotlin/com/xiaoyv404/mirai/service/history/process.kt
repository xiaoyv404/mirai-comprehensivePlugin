package com.xiaoyv404.mirai.service.history

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.service.getUserInformation
import com.xiaoyv404.mirai.service.tool.FileUtils.saveFileFromString
import kotlinx.serialization.SerializationException
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.MessageChain.Companion.serializeToJsonString
import net.mamoe.mirai.message.data.ids
import net.mamoe.mirai.message.data.time
import net.mamoe.mirai.utils.MiraiExperimentalApi

@MiraiExperimentalApi
fun historyEntrance() {
    GlobalEventChannel.subscribeGroupMessages {
        always {
            if (getUserInformation(sender.id).bot != true) {
                saveFileFromString(
                    message.contentToString() + "\n",
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
            } catch (e: SerializationException) {
            }
        }
    }
}