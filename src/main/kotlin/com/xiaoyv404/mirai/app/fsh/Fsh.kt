package com.xiaoyv404.mirai.app.fsh

import com.xiaoyv404.mirai.PluginData
import com.xiaoyv404.mirai.app.thesaurus.cMsgToMiraiMsg
import com.xiaoyv404.mirai.app.thesaurus.parseMsg
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.dao.authorityIdentification
import com.xiaoyv404.mirai.dao.findByQuestion
import com.xiaoyv404.mirai.dao.isBot
import com.xiaoyv404.mirai.model.Thesauru
import com.xiaoyv404.mirai.tool.CommandSplit
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.code.MiraiCode
import org.apache.commons.cli.UnrecognizedOptionException


@App
class Fsh : NfAppMessageHandler() {
    override fun getAppName() = "fsh"
    override fun getVersion() = "1.0.2"
    override fun getAppDescription() = "命令系统的底层实现"
    override fun getAppUsage() = "命令系统的底层实现模块, 具体使用见命令"

    private val debug get() = PluginData.deBug

    override suspend fun handleMessage(msg: MessageEvent) {
        val uid = msg.uid()
        val gid = msg.gid()
        val argsList = CommandSplit.splitWhit404(msg.message.contentToString()) ?: return

        if (msg.isBot())
            return

        // 最后参数的结果
        val fshApp = NfApplicationManager.fshCommands[argsList[0]]
        if (fshApp != null) {
            fshApp as NfApp
            fshApp.requireCallLimiter(msg, uid, gid, fshApp.getLimitHint()) {
                try {
                    val cmdLine = IFshApp.cmdLine(fshApp.getOptions(), argsList.toTypedArray())

                    if (cmdLine.hasOption("help")) {
                        msg.reply(fshApp.help(), quote = true)
                        return@requireCallLimiter
                    }
                    if (fshApp.executeRsh(argsList.toTypedArray(), msg)) {
                        // 调用成功进行限制计次
                        fshApp.submitCallLimiter(uid, gid)
                        CommandHistory.add(argsList[0], msg)
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