package com.xiaoyv404.mirai.service.helper

import com.xiaoyv404.mirai.databace.Command
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.code.MiraiCode

fun bugReporterEntrance() {
    GlobalEventChannel.subscribeGroupMessages {
        matching(Command.bugReport) {
            val rd = it.groups
            if ((rd[2]!!.value == "-h") || (rd[2]!!.value == "--help"))
                group.sendMessage("help")
            else {
                val msg =
                    StringBuffer("${sender.nick}(${sender.id})在${group.name}(${group.id})向${rd[5]!!.value}汇报Bug:\n")
                msg.append(
                    Command.bugReport.matchEntire(
                        message
                            .serializeToMiraiCode()
                    )!!.groups[6]?.value ?: ""
                )

                when (rd[5]!!.value) {
                    "404", "bot" -> {
                        bot.getFriendOrFail(2083664136)
                            .sendMessage(MiraiCode.deserializeMiraiCode(msg.substring(0)))
                        group.sendMessage("Bug已汇报")
                    }
                    "MCG"        -> {
                        bot.getFriendOrFail(2984959493)
                            .sendMessage(MiraiCode.deserializeMiraiCode(msg.substring(0)))
                        group.sendMessage("Bug已汇报")
                    }
                    else         -> {
                        group.sendMessage("无法找到项目组")
                    }
                }
            }
        }
    }
}