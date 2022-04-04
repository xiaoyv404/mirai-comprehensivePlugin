package com.xiaoyv404.mirai.app.fsh

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.app.thesaurus.cMsgToMiraiMsg
import com.xiaoyv404.mirai.app.thesaurus.parseMsg
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.core.NfAppMessageHandler
import com.xiaoyv404.mirai.core.NfApplicationManager
import com.xiaoyv404.mirai.databace.dao.Thesauru
import com.xiaoyv404.mirai.databace.dao.authorityIdentification
import com.xiaoyv404.mirai.databace.dao.findByQuestion
import com.xiaoyv404.mirai.databace.dao.isBot
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.data.OnlineMessageSource
import java.util.regex.Pattern


@App
class Fsh : NfAppMessageHandler(){
    override fun getAppName() = "fsh"
    override fun getVersion() = "1.0"
    override fun getAppDescription() = "命令系统的底层实现"
    override fun getAppUsage() = "命令系统的底层实现模块, 具体使用见命令"

    private val log = PluginMain.logger

    private val argsSplitPattern = Pattern.compile("([^\"]\\S*|\".+?(?<!\\\\)\")\\s*")

    override suspend fun handleMessage(msg: MessageEvent) {
        val incoming = msg.source
        val uid: Long
        val gid: Long
        when (incoming) {
            is OnlineMessageSource.Incoming.FromGroup  -> {
                gid = incoming.group.id
                uid = incoming.sender.id
            }
            is OnlineMessageSource.Incoming.FromFriend -> {
                gid = 0
                uid = incoming.sender.id
            }
            else                                       -> {
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
            fshApp.requireCallLimiter(msg, uid, gid) {
                try {
                    if (fshApp.executeRsh(argsList.toTypedArray(), msg)) {
                        // 调用成功进行限制计次
                        fshApp.submitCallLimiter(uid, gid)
                    }
                } catch (e: Exception) {
                    msg.reply("执行时发生内部错误", quote = true)
                    log.warning("处理${argsList[0]}命令发生异常\n$e")
                }
            }
        } else {
            if (authorityIdentification(
                    uid, gid, "ThesaurusResponse"
                )
            ) {
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
}