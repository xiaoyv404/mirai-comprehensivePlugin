package com.xiaoyv404.mirai.service.thesaurus

import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.Thesaurus
import com.xiaoyv404.mirai.service.Thesauru
import com.xiaoyv404.mirai.service.getUserInformation
import com.xiaoyv404.mirai.service.permissionRead
import com.xiaoyv404.mirai.service.queryTerm
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.nextMessage
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert

fun thesaurusEntrance() {
    GlobalEventChannel.subscribeMessages {
        finding(Regex("^(!!��������)\$")) {
            if (permissionRead(sender.id, subject.id, "ThesaurusAdd")) {
                subject.sendMessage("�뷢��question")
                val question = nextMessage().serializeToMiraiCode()
                subject.sendMessage("�뷢��reply")
                val reply = nextMessage().serializeToMiraiCode()
                subject.sendMessage(
                    "question: $question\n" +
                        "reply: $reply\n"
                        + "������[y]��ȷ��"
                )
                if (nextMessage().contentToString() == "y")
                    increaseEntry(question, reply, sender.id)
                subject.sendMessage("success")
            }
        }

        finding(Command.thesaurusRemove) {
            if (getUserInformation(sender.id).admin == true) {
                subject.sendMessage("�뷢��question")
                val entryMassages = queryTerm(nextMessage().serializeToMiraiCode())
                if (entryMassages.isEmpty()) {
                    subject.sendMessage("����û����")
                } else {
                    if (entryMassages.size == 1) {
                        subject.sendMessage(thesaurusRemoveMsg(entryMassages[0]))
                    } else {
                        subject.sendMessage(
                            buildForwardMessage {
                                entryMassages.forEach { da ->
                                    bot.says(thesaurusRemoveMsg(da))
                                }
                            }
                        )
                    }
                    subject.sendMessage("�뷢��Ҫɾ���Ĵ������±�")
                    val subscript = nextMessage().contentToString()
                    if (!(Regex("[0-9]+").containsMatchIn(subscript))) {
                        subject.sendMessage("��ȡ��")
                        return@finding
                    }
                    val subscriptI = subscript.toInt()
                    subject.sendMessage(
                        "ȷ��Ҫɾ��: \n" +
                            "${thesaurusRemoveMsg(entryMassages[subscriptI])}\n" +
                            "����[y]��ȷ��    ����[n]��ȡ��"
                    )
                    if (nextMessage().contentToString() == "y") {
                        thesaurusRemove(entryMassages[subscriptI].id)
                        subject.sendMessage("success")
                    } else {
                        subject.sendMessage("ȡ��")
                    }
                }
            }
        }
    }
}

fun thesaurusRemoveMsg(da: Thesauru): String {
    return "ID: ${da.id}\n" +
        "   question: ${da.question}\n" +
        "   reply: ${da.reply}\n" +
        "   creator id: ${da.creator}"
}

fun thesaurusRemove(id: Long) {
    Database.db
        .delete(Thesaurus) { it.id eq id }
}

fun increaseEntry(question: String, reply: String, creator: Long) {
    Database.db
        .insert(Thesaurus) {
            set(it.question, question)
            set(it.reply, reply)
            set(it.creator, creator)
        }
}
