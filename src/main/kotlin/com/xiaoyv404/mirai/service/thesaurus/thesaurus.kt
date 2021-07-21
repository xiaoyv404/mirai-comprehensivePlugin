package com.xiaoyv404.mirai.service.thesaurus

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.Thesaurus
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.nextMessage
import org.ktorm.dsl.insert

fun thesaurusEntrance() {
    GlobalEventChannel.subscribeMessages {
        finding(Regex("^(!!��������)\$")) {
            subject.sendMessage("�뷢��question")
            val question = nextMessage().serializeToMiraiCode()
            subject.sendMessage("�뷢��reply")
            val reply = nextMessage().serializeToMiraiCode()
            increaseEntry(question, reply, sender.id)
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
