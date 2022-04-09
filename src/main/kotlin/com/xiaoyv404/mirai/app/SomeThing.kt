package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.core.gid
import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.databace.dao.*
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.contact.remarkOrNameCardOrNick
import net.mamoe.mirai.contact.remarkOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.nextMessage
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Options
import kotlin.coroutines.EmptyCoroutineContext

@App
class SomeThing : NfApp(), IFshApp {
    override fun getAppName() = "SomeThing"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "杂七杂八的东西"
    override fun getCommands(): Array<String> =
        arrayOf("~me", "-status", "-help", "-sendto", "-bot", "-ban", "!!开启全体广播")

    private val banOptions = Options().apply {
        addOption("g", "group", true, "群聊ID")
        addOption("u", "unBan", false, "取消禁言")
        addOption("t", "time", true, "禁言时间")
    }

    private val log = PluginMain.logger

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        when (args[0]) {
            "~me"      -> debuMe(args.getOrNull(2), msg)
            "-status"  -> status(msg)
            "-help"    -> help(msg)
            "-sendto"  -> sendto(msg)
            "-bot"     -> {
                println(args.getOrNull(2))
                if (args[1] == "add")
                    addBot(args.getOrNull(2) ?: return false, msg)
            }
            "-ban"     -> {
                ban((args.getOrNull(1) ?: return false).toLong(), IFshApp.cmdLine(banOptions, args), msg)
            }
            "!!开启全体广播" -> adminBroadcast(msg)
        }
        return true
    }

    private suspend fun debuMe(data: String?, msg: MessageEvent) {
        val sender = msg.sender

        if (authorityIdentification(
                msg.uid(),
                msg.gid(),
                "DebuMe"
            )
        ) {
            val name = data ?: if (sender is Member) {
                sender.remarkOrNameCardOrNick
            } else
                sender.remarkOrNick
            msg.reply(
                "*${name}坐在地上哭着说道「可怜哒${name}什么时候才有大佬们百分之一厉害呀……」"
            )
        }
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

    private suspend fun sendto(msg: MessageEvent) {
        if (msg.isNotAdmin())
            return

        msg.reply("请发送群id")
        val gpIds = Regex("\\d+").findAll(msg.nextMessage().contentToString()).toList()
        log.info("群聊个数${gpIds.size}")
        msg.reply("请发送msg")
        val nextMsg = msg.nextMessage()
        gpIds.forEach {
            msg.bot.getGroup(it.value.toLong())?.sendMessage(nextMsg)
        }
    }

    private suspend fun addBot(data: String, msg: MessageEvent) {
        if (msg.isNotAdmin())
            return

        val idA = (Regex("\\d+").find(data) ?: return).value.toLong()
        User {
            id = idA
            bot = true
        }.save()
        msg.reply("添加成功~")

    }

    private suspend fun ban(uid: Long, data: CommandLine, msg: MessageEvent) {
        if (msg.isNotAdmin())
            return

        val gid = if (data.hasOption("group"))
            data.getOptionValue("group").toLong()
        else if (msg.gid() != 0L)
            msg.gid()
        else {
            msg.reply("缺少参数: groupId")
            return
        }


        val time = if (data.hasOption("time"))
            data.getOptionValue("time").toInt()
        else
            60

        val target = try {
            val group = msg.subject.bot
                .getGroupOrFail(gid)
            if (group.botAsMember.permission == MemberPermission.MEMBER) {
                msg.reply("404没有权限qwq")
                return
            }
            group.getOrFail(uid)
        } catch (e: NoSuchElementException) {
            msg.reply("无效的群或成员")
            return
        }


        if (data.hasOption("unBan"))
            target.unmute()
        else
            target.mute(time)
    }

    private suspend fun adminBroadcast(msg: MessageEvent) {
        if (msg.isNotAdmin())
            return

        msg.reply("全体广播已开启")
        GlobalEventChannel.subscribe<FriendMessageEvent> {
            if (msg.uid() == it.sender.id) {
                val entryMassage = it.message.serializeToMiraiCode()
                if (entryMassage == "!!关闭全体广播") {
                    msg.reply("全体广播已关闭")
                    return@subscribe ListeningStatus.STOPPED
                }
                if (entryMassage != "") {
                    bot.groups.forEach { gp ->
                        val status = Group {
                            id = gp.id
                        }.noticeSwitchRead("AdminBroadcast")
                        if (status) {
                            bot.getGroup(gp.id)
                                ?.sendMessage(
                                    MiraiCode.deserializeMiraiCode(entryMassage)
                                )
                        }
                    }
                }
            }
            return@subscribe ListeningStatus.LISTENING
        }
    }

    override fun init() {
        GlobalEventChannel.subscribeAlways(
            BotInvitedJoinGroupRequestEvent::class,
            EmptyCoroutineContext
        ) {
            bot.getFriend(2083664136L)!!
                .sendMessage(
                    "事件ID: ${it.eventId}\n" +
                        "主人,${it.invitorNick}(${it.invitorId})邀请我加入群${it.groupName}(${it.groupId})"
                )
            accept()
        }
        GlobalEventChannel.subscribeGroupMessages {
            at(2083664136L).invoke {
                var chain = buildMessageChain {
                    +PlainText("${sender.nick}(${sender.id})在${group.name}(${group.id})中对主人说：\n")
                }
                chain = chain.plus(
                    MiraiCode.deserializeMiraiCode(
                        message
                            .serializeToMiraiCode()
                            .replace("[mirai:at:2083664136] ", "")
                            .replace("[mirai:at:2083664136]", "")
                    )
                )
                bot.getFriend(2083664136L)?.sendMessage(chain)
            }

            at(3068755284).invoke {
                var chain = buildMessageChain {
                    +PlainText("${sender.nick}(${sender.id})在${group.name}(${group.id})中对鸡哥说：\n")
                }
                chain = chain.plus(
                    MiraiCode.deserializeMiraiCode(
                        message
                            .serializeToMiraiCode()
                            .replace("[mirai:at:3068755284] ", "")
                            .replace("[mirai:at:3068755284]", "")
                    )
                )
                bot.getFriend(3068755284)?.sendMessage(chain)
            }
        }
    }
}