package com.xiaoyv404.mirai.app.thesaurus

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.databace.dao.*
import com.xiaoyv404.mirai.tool.FileUtils
import com.xiaoyv404.mirai.tool.KtorUtils
import io.ktor.client.request.*
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.nextMessage
import java.io.InputStream
import java.math.BigInteger

@App
class Thesaurus : NfApp(), IFshApp{

    override fun getAppName() = "Thesaurus"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "�ʿ�"
    override fun getCommands() = arrayOf("!!��������", "-thesaurus")

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        if (args[0] == "��������") {
            thesaurusAdd(msg.sender, msg)
            return true
        }
        if (args[1] == "remove") {
            return thesaurusRemove(msg.sender, msg, args.getOrNull(2)?.toLong())
        }
        return true
    }

    private suspend fun thesaurusAdd(sender: net.mamoe.mirai.contact.User, msg: MessageEvent) {
        val subject = msg.subject
        if (authorityIdentification(sender.id, subject.id, "ThesaurusAdd")) {
            subject.sendMessage("�뷢��question")
            val questionA = parseMsgAndSaveImg(msg.nextMessage())
            subject.sendMessage("�뷢��reply")
            val replyA = parseMsgAndSaveImg(msg.nextMessage())
            subject.sendMessage(
                "question: $questionA\n" +
                    "reply: $replyA\n"
                    + "������[y]��ȷ��"
            )
            if (msg.nextMessage().contentToString() == "y") {
                Thesauru {
                    question = questionA
                    reply = replyA
                    creator = sender.id
                }.save()
                subject.sendMessage("��ӳɹ�~")
            } else
                subject.sendMessage("����, ΪɶҪȡ����")
        }
    }

    private suspend fun thesaurusRemove(
        sender: net.mamoe.mirai.contact.User,
        msg: MessageEvent,
        gidInput: Long? = null
    ): Boolean {
        if (sender.isAdmin()) {
            val subject = msg.subject
            val gid = when {
                gidInput != null -> gidInput
                subject is Group -> subject.id
                else             -> {
                    subject.sendMessage("����ֵ����")
                    return false
                }
            }
            subject.sendMessage("�뷢��question")
            val entryMassages = Thesauru {
                question = parseMsg(msg.nextMessage())
            }.findByQuestion(gid)
            if (entryMassages.isEmpty()) {
                subject.sendMessage("����û����")
            } else {
                if (entryMassages.size == 1) {
                    subject.sendMessage(MiraiCode.deserializeMiraiCode(thesaurusRemoveMsg(entryMassages[0])))
                } else {
                    subject.sendMessage(
                        msg.buildForwardMessage {
                            entryMassages.forEach { da ->
                                da.question.cMsgToMiraiMsg(subject)
                                da.reply.cMsgToMiraiMsg(subject)
                                subject.bot.says(MiraiCode.deserializeMiraiCode(thesaurusRemoveMsg(da)))
                            }
                        }
                    )
                }
                subject.sendMessage("�뷢��Ҫɾ���Ĵ������±�")
                val subscript = msg.nextMessage().contentToString()
                if (!(Regex("[0-9]+").containsMatchIn(subscript))) {
                    subject.sendMessage("��ȡ��")
                    return true
                }
                val subscriptI = subscript.toInt()
                subject.sendMessage(
                    "ȷ��Ҫɾ��: \n" +
                        "${MiraiCode.deserializeMiraiCode(thesaurusRemoveMsg(entryMassages[subscriptI]))}\n" +
                        "����[y]��ȷ��    ����[n]��ȡ��"
                )
                if (msg.nextMessage().contentToString() == "y") {
                    Thesauru {
                        id = entryMassages[subscriptI].id
                    }.deleteById()
                    subject.sendMessage("�ɹ�ɾ��")
                } else {
                    subject.sendMessage("ΪʲôҪȡ����")
                }
            }
        }
        return true
    }
}

suspend fun String.cMsgToMiraiMsg(subject: Contact): String {
    Regex("(\\[404:image:(.+)])").findAll(this).forEach {
        val img =
            subject.uploadImage(PluginMain.resolveDataFile("thesaurus/${it.groups[2]!!.value}"))
                .serializeToMiraiCode()
        this.replace(it.value, img)
    }
    return this
}

fun thesaurusRemoveMsg(da: Thesauru): String {
    return("""ID: ${da.id}
   question: ${da.question}
   reply: ${da.reply}
   creator id: ${da.creator}""")
}

suspend fun parseMsgAndSaveImg(message: MessageChain): String {
    val img = mutableListOf<String>()
    message.forEach {
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