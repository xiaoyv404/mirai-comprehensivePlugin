package com.xiaoyv404.mirai.app.thesaurus

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.dao.*
import com.xiaoyv404.mirai.databace.entity.*
import com.xiaoyv404.mirai.entity.*
import com.xiaoyv404.mirai.tool.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.*
import net.mamoe.mirai.message.code.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import java.io.*
import java.math.*

@App
class Thesaurus : NfApp(), IFshApp {

    override fun getAppName() = "Thesaurus"
    override fun getVersion() = "1.0.2"
    override fun getAppDescription() = "词库"
    override fun getCommands() = arrayOf("!!创建词条", "-thesaurus")

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        if (args[0] == "!!创建词条") {
            return add(msg)
        }
        if (args[1] == "remove") {
            return remove(msg, args.getOrNull(2)?.toLong())
        }
        return true
    }

    private suspend fun add(msg: MessageEvent): Boolean {
        val subject = msg.subject

        if (msg.authorityIdentification("ThesaurusAdd"))
            return false

        subject.sendMessage("请发送question")
        val questionA = parseMsgAndSaveImg(msg.nextMessage())
        subject.sendMessage("请发送reply")
        val replyA = parseMsgAndSaveImg(msg.nextMessage())
        subject.sendMessage(
            "question: $questionA\nreply: $replyA\n请输入[y]以确认"
        )
        if (msg.nextMessage().contentToString() == "y") {
            Thesauru {
                question = questionA
                reply = replyA
                creator = msg.uid()
            }.save()
            subject.sendMessage("添加成功~")
        } else subject.sendMessage("啊咧, 为啥要取消捏")

        return true
    }

    private suspend fun remove(
        msg: MessageEvent, gidInput: Long? = null
    ): Boolean {
        if (msg.isNotAdmin()) return false

        val subject = msg.subject
        val gid = when {
            gidInput != null -> gidInput
            msg.gid() != 0L  -> msg.gid()
            else             -> {
                msg.reply("输入值错误", true)
                return false
            }
        }
        msg.reply("请发送question")

        val entryMassages = Thesauru {
            question = parseMsg(msg.nextMessage())
        }.findByQuestion(gid)
        if (entryMassages.isEmpty()) {
            subject.sendMessage("好像没有呢")
            return true
        }

        if (entryMassages.size == 1) {
            subject.sendMessage(MiraiCode.deserializeMiraiCode(thesaurusRemoveMsg(entryMassages[0])))
        } else {
            subject.sendMessage(msg.buildForwardMessage {
                entryMassages.forEach { da ->
                    da.question.cMsgToMiraiMsg(subject)
                    da.reply.cMsgToMiraiMsg(subject)
                    subject.bot.says(MiraiCode.deserializeMiraiCode(thesaurusRemoveMsg(da)))
                }
            })
        }

        subject.sendMessage("请发送要删除的词条的下标")
        val subscript = msg.nextMessage().contentToString()
        if (!(Regex("\\d+").containsMatchIn(subscript))) {
            subject.sendMessage("已取消")
            return true
        }

        val subscriptI = subscript.toInt()
        subject.sendMessage(
            "确定要删除: \n" + "${MiraiCode.deserializeMiraiCode(thesaurusRemoveMsg(entryMassages[subscriptI]))}\n" + "输入[y]以确认    输入[n]以取消"
        )

        if (msg.nextMessage().contentToString() == "y") {
            Thesauru {
                id = entryMassages[subscriptI].id
            }.deleteById()
            subject.sendMessage("成功删除")
        } else {
            subject.sendMessage("为什么要取消捏")
        }

        return true
    }

    private fun thesaurusRemoveMsg(da: Thesauru): String {
        return ("""ID: ${da.id}
   question: ${da.question}
   reply: ${da.reply}
   creator id: ${da.creator}""")
    }
}

suspend fun String.cMsgToMiraiMsg(subject: Contact): String {
    var msg = this
    Regex("(\\[404:image:(.+)])").findAll(this).forEach {
        val img =
            subject.uploadImage(PluginMain.resolveDataFile("thesaurus/${it.groups[2]!!.value}")).serializeToMiraiCode()
        msg = msg.replace(it.value, img)
    }
    return msg
}

suspend fun parseMsgAndSaveImg(message: MessageChain): String {
    val img = mutableListOf<String>()
    message.forEach {
        if (it is Image) {
            val imageId = BigInteger(1, it.md5).toString(16)
            val `in` = ClientUtils.get<InputStream>(it.queryUrl())
            val imageType = if (it.imageType != ImageType.UNKNOWN) it.imageType
            else ImageType.PNG

            FileUtils.saveFileFromStream(
                `in`, PluginMain.resolveDataFile("thesaurus/$imageId.$imageType")
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
            val imageType = if (it.imageType != ImageType.UNKNOWN) it.imageType
            else ImageType.PNG
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