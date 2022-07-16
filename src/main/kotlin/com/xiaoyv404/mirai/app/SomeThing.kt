package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.extension.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.code.*
import net.mamoe.mirai.message.data.*
import kotlin.coroutines.*

@App
class SomeThing : NfApp(), IFshApp {
    override fun getAppName() = "SomeThing"
    override fun getVersion() = "1.1.1"
    override fun getAppDescription() = "杂七杂八的东西"
    override fun getCommands(): Array<String> =
        arrayOf("-status", "-help", "-test")


    private val eventList get() = NfPluginData.eventMap

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        when (args[0]) {
            "-status" -> status(msg)
            "-help" -> help(msg)
            "-test" -> test(msg)
        }
        return true
    }

    private suspend fun test(msg: MessageEvent) {
        msg.reply("test")
    }

    private suspend fun status(msg: MessageEvent) {
        val bot = msg.bot
        msg.reply(
            "Bot: ${bot.nick}(${bot.id})\n" +
                "status: Online "
        )
    }

    private suspend fun help(msg: MessageEvent) {
        msg.reply("https://www.xiaoyv404.top/archives/404.html")
    }

    override fun init() {
        GlobalEventChannel.subscribeAlways(
            BotInvitedJoinGroupRequestEvent::class,
            EmptyCoroutineContext
        ) {
            (2083664136L).getFriend()!!
                .sendMessage(
                    "事件ID: ${it.eventId}\n" +
                        "主人,${it.invitorNick}(${it.invitorId})邀请我加入群${it.groupName}(${it.groupId})"
                )
            accept()
        }

        GlobalEventChannel.subscribeAlways(
            NewFriendRequestEvent::class
        ) {
            eventList[it.eventId] = NfNewFriendRequestEvent(
                it.eventId,
                it.message,
                it.fromId,
                it.fromGroupId,
                it.fromNick
            )
            (2083664136L).getFriend()!!.sendMessage(
                """
                [事件]好友添加请求    事件ID: ${it.eventId}
                来自 ${it.fromNick}(${it.fromId})的请求
                验证消息: ${it.message}
                """.trimIndent()
            )
        }
        GlobalEventChannel.subscribeGroupMessages {
            at(2083664136L).invoke {
                var chain = buildMessageChain {
                    +PlainText("${sender.nick}(${sender.id})在${group.name}(${group.id})中对主人说：\n")
                }
                chain = chain.plus(
                    MiraiCode.deserializeMiraiCode(
                        message
                            .serializeToMiraiCode().replace(Regex("^(\\[mirai:at:2083664136]( )?)"), "")
                    )
                )
                (2083664136L).getFriend()!!.sendMessage(chain)
            }

            at(3068755284).invoke {
                var chain = buildMessageChain {
                    +PlainText("${sender.nick}(${sender.id})在${group.name}(${group.id})中对鸡哥说：\n")
                }
                chain = chain.plus(
                    MiraiCode.deserializeMiraiCode(
                        message
                            .serializeToMiraiCode().replace(Regex("^(\\[mirai:at:3068755284]( )?)"), "")
                    )
                )
                (2083664136L).getFriend()!!.sendMessage(chain)
            }
        }
    }
}