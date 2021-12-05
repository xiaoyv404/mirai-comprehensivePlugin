package com.xiaoyv404.mirai.service.thesaurus

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.Thesaurus
import com.xiaoyv404.mirai.service.Thesauru
import com.xiaoyv404.mirai.service.accessControl.authorityIdentification
import com.xiaoyv404.mirai.service.getUserInformation
import com.xiaoyv404.mirai.service.tool.FileUtils
import com.xiaoyv404.mirai.service.tool.KtorUtils
import com.xiaoyv404.mirai.service.tool.jsonExtractContains
import io.ktor.client.request.*
import io.ktor.util.*
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.nextMessage
import net.mamoe.mirai.utils.MiraiInternalApi
import org.ktorm.dsl.*
import org.ktorm.schema.VarcharSqlType
import java.io.InputStream
import java.math.BigInteger


@KtorExperimentalAPI
@MiraiInternalApi
fun thesaurusEntrance() {
    GlobalEventChannel.subscribeMessages {
        finding(Regex("^(!!��������)\$")) {
            if (authorityIdentification(sender.id, subject.id, "ThesaurusAdd")) {
                subject.sendMessage("�뷢��question")
                val question = parseMsgAndSaveImg(nextMessage())
                subject.sendMessage("�뷢��reply")
                val reply = parseMsgAndSaveImg(nextMessage())
                subject.sendMessage(
                    "question: $question\n" +
                        "reply: $reply\n"
                        + "������[y]��ȷ��"
                )
                if (nextMessage().contentToString() == "y") {
                    increaseEntry(question, reply, sender.id)
                    subject.sendMessage("��ӳɹ�~")
                } else
                    subject.sendMessage("����, ΪɶҪȡ����")
            }
        }

        finding(Command.thesaurusRemove) {
            if (getUserInformation(sender.id).admin == true) {
                val gp = it.groups
                val gid = when {
                    subject is net.mamoe.mirai.contact.Group -> subject.id
                    gp[5] != null                            -> gp[5]!!.value.toLong()
                    else                                     -> {
                        subject.sendMessage("����ֵ����")
                        return@finding
                    }
                }
                subject.sendMessage("�뷢��question")
                val entryMassages = queryTerm(nextMessage(), gid)
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
                        subject.sendMessage("�ɹ�ɾ��")
                    } else {
                        subject.sendMessage("ΪʲôҪȡ����")
                    }
                }
            }
        }
    }
    GlobalEventChannel.subscribeGroupMessages {
        always {
            if ((getUserInformation(sender.id).bot != true) && authorityIdentification(
                    sender.id,
                    group.id,
                    "ThesaurusResponse"
                )
            ) {
                val replyC = queryTerm(message, group.id)
                if (replyC.isEmpty())
                    return@always
                var reply =  replyC.random().reply
                Regex("(\\[404:image:(.+)])").findAll(reply).forEach {
                    val img =
                        group.uploadImage(PluginMain.resolveDataFile("thesaurus/${it.groups[2]!!.value}"))
                            .serializeToMiraiCode()
                    reply = reply.replace(it.value, img)
                }
                group.sendMessage(MiraiCode.deserializeMiraiCode(reply))
            }
        }
    }
}


fun thesaurusRemoveMsg(da: Thesauru): String {
    return("""ID: ${da.id}
   question: ${da.question}
   reply: ${da.reply}
   creator id: ${da.creator}""")
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
suspend fun parseMsgAndSaveImg(message: MessageChain): String {
    val img = mutableListOf<String>()
    message.toMessageChain().forEach {
        if (it is Image) {
            val imageId = BigInteger(1, it.md5).toString(16)
            val `in` = KtorUtils.normalClient.get<InputStream>(it.queryUrl())
            val imageType = if (it.imageType != ImageType.UNKNOWN)
                it.imageType
            else
                ImageType.PNG

            FileUtils.saveFileFromStream(
                `in`,
                PluginMain.resolveDataFile("thesaurus/$imageId.$imageType")
            )
            img.add("[404:image:${imageId}.$imageType]")
        }
    }

    var msg = message.serializeToMiraiCode()
    val matchImg = Regex("^\\[mirai:image:.+]\$").findAll(msg)
    for ((i, v) in matchImg.withIndex()) {
        msg = msg.replace(v.value, img[i])
    }

    return msg
}

@KtorExperimentalAPI
@MiraiInternalApi
fun parseMsg(message: MessageChain): String {
    val img = mutableListOf<String>()
    message.toMessageChain().forEach {
        if (it is Image) {
            val imageId = BigInteger(1, it.md5).toString(16)
            val imageType = if (it.imageType != ImageType.UNKNOWN)
                it.imageType
            else
                ImageType.PNG
            img.add("[404:image:${imageId}.$imageType]")
        }
    }
    var msg = message.serializeToMiraiCode()
    val matchImg = Regex("^\\[mirai:image:.+]\$").findAll(msg)
    for ((i, v) in matchImg.withIndex()) {
        msg = msg.replace(v.value, img[i])
    }
    return msg
}

@KtorExperimentalAPI
@MiraiInternalApi
fun queryTerm(question: MessageChain, gid: Long): List<Thesauru> {
    val sQuestion = parseMsg(question)
    val sGid = gid.toString()
    return Database.db
        .from(Thesaurus)
        .select()
        .where {
            Thesaurus.question eq sQuestion and
                (Thesaurus.scope.jsonExtractContains("$", sGid, VarcharSqlType) or Thesaurus.scope.isNull())
        }.map { row ->
            Thesauru(
                row[Thesaurus.id]!!,
                row[Thesaurus.question]!!,
                row[Thesaurus.reply]!!,
                row[Thesaurus.creator]!!,
            )
        }
}