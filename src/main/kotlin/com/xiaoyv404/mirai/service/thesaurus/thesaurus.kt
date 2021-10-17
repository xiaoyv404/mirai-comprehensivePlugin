package com.xiaoyv404.mirai.service.thesaurus

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.Thesaurus
import com.xiaoyv404.mirai.service.Thesauru
import com.xiaoyv404.mirai.service.accessControl.authorityIdentification
import com.xiaoyv404.mirai.service.getUserInformation
import com.xiaoyv404.mirai.service.queryTerm
import com.xiaoyv404.mirai.service.tool.FileUtils
import com.xiaoyv404.mirai.service.tool.KtorUtils
import io.ktor.client.request.*
import io.ktor.util.*
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.nextMessage
import net.mamoe.mirai.utils.MiraiInternalApi
import org.apache.commons.lang3.StringUtils
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import java.io.InputStream
import java.math.BigInteger


fun thesaurusEntrance() {
    GlobalEventChannel.subscribeMessages {
        finding(Regex("^(!!创建词条)\$")) {
            if (authorityIdentification(sender.id, subject.id, "ThesaurusAdd")) {
                subject.sendMessage("请发送question")
                val question = nextMessage().serializeToMiraiCode()
                subject.sendMessage("请发送reply")
                val reply = nextMessage().serializeToMiraiCode()
                subject.sendMessage(
                    "question: $question\n" +
                        "reply: $reply\n"
                        + "请输入[y]以确认"
                )
                if (nextMessage().contentToString() == "y") {
                    increaseEntry(question, reply, sender.id)
                    subject.sendMessage("添加成功~")
                } else
                    subject.sendMessage("啊咧, 为啥要取消捏")
            }
        }

        finding(Command.thesaurusRemove) {
            if (getUserInformation(sender.id).admin == true) {
                val gp = it.groups
                val gid = when {
                    subject is net.mamoe.mirai.contact.Group -> subject.id
                    gp[5] != null -> gp[5]!!.value.toLong()
                    else                                     -> {
                        subject.sendMessage("输入值错误")
                        return@finding
                    }
                }
                subject.sendMessage("请发送question")
                val entryMassages = queryTerm(nextMessage().serializeToMiraiCode(), gid)
                if (entryMassages.isEmpty()) {
                    subject.sendMessage("好像没有呢")
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
                    subject.sendMessage("请发送要删除的词条的下标")
                    val subscript = nextMessage().contentToString()
                    if (!(Regex("[0-9]+").containsMatchIn(subscript))) {
                        subject.sendMessage("已取消")
                        return@finding
                    }
                    val subscriptI = subscript.toInt()
                    subject.sendMessage(
                        "确定要删除: \n" +
                            "${thesaurusRemoveMsg(entryMassages[subscriptI])}\n" +
                            "输入[y]以确认    输入[n]以取消"
                    )
                    if (nextMessage().contentToString() == "y") {
                        thesaurusRemove(entryMassages[subscriptI].id)
                        subject.sendMessage("success")
                    } else {
                        subject.sendMessage("取消")
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

@KtorExperimentalAPI
@MiraiInternalApi
suspend fun parseAndSaveMsg(message: Message) {
    val msg = mutableListOf<String>()
    message.toMessageChain().forEach {
        if (it is Image) {
            val imageId = BigInteger(1, it.md5).toString(16)
            val `in` = KtorUtils.normalClient.get<InputStream>(it.queryUrl())
            val imageType = if (it.imageType == ImageType.UNKNOWN)
                it.imageType
            else
                ImageType.PNG

            FileUtils.saveFileFromStream(
                `in`,
                PluginMain.resolveDataFile("thesaurus/$imageId.$imageType")
            )
            msg.add("[404:image:${imageId}.$imageType]")
        } else {
            msg.add(it.toString())
        }
    }

    println(StringUtils.join(msg.drop(1), ""))
}   