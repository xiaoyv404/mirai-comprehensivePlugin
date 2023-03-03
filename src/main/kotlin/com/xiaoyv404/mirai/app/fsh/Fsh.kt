package com.xiaoyv404.mirai.app.fsh

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.app.thesaurus.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.dao.*
import com.xiaoyv404.mirai.entity.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.code.*
import net.mamoe.mirai.message.data.*
import org.apache.commons.cli.*
import java.util.regex.*


@App
class Fsh : NfAppMessageHandler() {
    override fun getAppName() = "fsh"
    override fun getVersion() = "1.0.1"
    override fun getAppDescription() = "命令系统的底层实现"
    override fun getAppUsage() = "命令系统的底层实现模块, 具体使用见命令"

    private val argsSplitPattern = Pattern.compile("([^\"]\\S*|\".+?(?<!\\\\)\")\\s*")
    private val debug get() = NfPluginData.deBug

    override suspend fun handleMessage(msg: MessageEvent) {
        val incoming = msg.source
        val uid: Long
        val gid: Long
        when (incoming) {
            is OnlineMessageSource.Incoming.FromGroup -> {
                gid = incoming.group.id
                uid = incoming.sender.id
            }

            is OnlineMessageSource.Incoming.FromFriend -> {
                gid = 0
                uid = incoming.sender.id
            }

            else -> {
                return
            }
        }
        val line = msg.message.contentToString().trim()

        // 分割参数
        val matcher = argsSplitPattern.matcher(line)
        val argsList: ArrayList<String> = ArrayList()
        while (matcher.find()) {
            var s = matcher.group(1)
            if (s.startsWith("\"") && s.endsWith("\"") && s.length > 1) {
                s = s.substring(1, s.length - 1)
            }
            s = s.replace("\\\"", "\"")
            argsList.add(s)
        }

        if (argsList.isEmpty())
            return

        if (argsList.size > 1 && argsList[0] == "404") {
            argsList.removeAt(0)
            argsList[0] = "-${argsList[0]}"
        }

        if (uid.isBot())
            return

        // 最后参数的结果
        val fshApp = NfApplicationManager.fshCommands[argsList[0]]
        if (fshApp != null) {
            fshApp as NfApp
            fshApp.requireCallLimiter(msg, uid, gid, fshApp.getLimitHint()) {
                try {
                    if (fshApp.executeRsh(argsList.toTypedArray(), msg)) {
                        // 调用成功进行限制计次
                        fshApp.submitCallLimiter(uid, gid)
                    }
                } catch (_: UnrecognizedOptionException) {
                    msg.reply("未知参数")
                } catch (e: Exception) {
                    if (debug) {
                        msg.reply("处理${argsList[0]}命令发生异常\n$e", quote = true)
                        log.warning("处理${argsList[0]}命令发生异常")
                        e.printStackTrace()
                    } else {
                        msg.reply("执行时发生内部错误", quote = true)
                        log.warning("处理${argsList[0]}命令发生异常\n$e")
                    }
                }
            }
        } else {
            if (msg.authorityIdentification("ThesaurusResponse"))
                return

            val replyC = Thesauru {
                question = parseMsg(msg.message)
            }.findByQuestion(gid)
            if (replyC.isEmpty())
                return
            val reply = replyC.random().reply.cMsgToMiraiMsg(msg.subject)
            msg.reply(MiraiCode.deserializeMiraiCode(reply), quote = false)
        }
    }
}