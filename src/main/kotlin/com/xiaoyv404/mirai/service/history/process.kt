package com.xiaoyv404.mirai.service.history

import com.xiaoyv404.mirai.PluginConfig
import com.xiaoyv404.mirai.service.getUserInformation
import com.xiaoyv404.mirai.service.tool.FileUtils.saveFileFromString
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import java.io.File

fun save() {
    GlobalEventChannel.subscribeGroupMessages {
        always {
            if (getUserInformation(sender.id).bot != true) {
                saveFileFromString(
                    message.contentToString() + "\n",
                    File("${PluginConfig.database.SaveHistory}${group.id}.txt")
                )
            }
        }
    }
}