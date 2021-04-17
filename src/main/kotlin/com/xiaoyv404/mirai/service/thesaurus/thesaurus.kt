package com.xiaoyv404.mirai.service.thesaurus

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.Thesaurus
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import org.ktorm.dsl.insert


var senderId: Long = 0
var SendFrequency: Int = 0

var question: String = ""
var reply: String = ""

fun thesaurusEntrance() {
    GlobalEventChannel.subscribeGroupMessages {
        always {
            if (senderId == sender.id) {
                if (SendFrequency == 2) {
                    reply = message.serializeToMiraiCode()
                    SendFrequency = 0
                    increaseEntry(question, reply, senderId)
                    group.sendMessage("success")
                }
                if (SendFrequency == 1) {
                    question = message.serializeToMiraiCode()
                    group.sendMessage("请发送reply")
                    SendFrequency++
                }
            }
            if (message.contentToString() == "!!创建词条") {
                SendFrequency = 0
                senderId = sender.id
                group.sendMessage("请发送question")
                SendFrequency++
            }
        }
    }
}

fun increaseEntry(question: String, reply: String, creator: Long) {
    Database.db
        .insert(Thesaurus) {
            set(it.question, question)
            set(it.reply, reply)
            set(it.creator, creator)
        }
}
