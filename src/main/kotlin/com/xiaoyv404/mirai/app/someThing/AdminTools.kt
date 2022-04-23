package com.xiaoyv404.mirai.app.someThing

import com.xiaoyv404.mirai.NfPluginData
import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.core.gid
import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.databace.dao.*
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.nextMessage
import net.mamoe.mirai.utils.MiraiInternalApi
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Options

@App
class AdminTools : NfApp(), IFshApp {
    override fun getAppName() = "AdminTools"
    override fun getVersion() = "1.0.1"
    override fun getAppDescription() = "管理员管理工具"
    override fun getCommands() = arrayOf("-sendto", "-ban", "!!开启全体广播", "-bot", "-accept","-debug")

    private val banOptions = Options().apply {
        addOption("g", "group", true, "群聊ID")
        addOption("u", "unBan", false, "取消禁言")
        addOption("t", "time", true, "禁言时间")
    }

    private val eventList get() = NfPluginData.eventMap

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        when (args[0]) {
            "-sendto"  -> sendto(msg)
            "-bot"     -> {
                if (args[1] == "add")
                    addBot(args.getOrNull(2) ?: return false, msg)
            }
            "-ban"     -> {
                ban((args.getOrNull(1) ?: return false).toLong(), IFshApp.cmdLine(banOptions, args), msg)
            }
            "!!开启全体广播" -> adminBroadcast(msg)
            "-accept"  -> accept(msg, args.getOrNull(1)?.toLongOrNull() ?: return false)
            "-debug"   -> debug(msg, args.getOrNull(1)?.toBooleanStrictOrNull() ?: return false)
        }
        return true
    }

    private suspend fun debug(msg: MessageEvent, switch: Boolean) {
        NfPluginData.deBug = switch
        msg.reply("Debug模式已切换至 $switch", true)
    }

    @OptIn(MiraiInternalApi::class)
    private suspend fun accept(msg: MessageEvent, eventID: Long) {
        val nfEvent = eventList.remove(eventID)
        if (nfEvent == null) {
            msg.reply("无此事件")
            return
        }

        val event = nfEvent.let {
            NewFriendRequestEvent(
                msg.bot,
                it.eventId,
                it.message,
                it.fromId,
                it.fromGroupId,
                it.fromNick
            )
        }
        event.accept()
        msg.reply("已同意事件 $eventID", true)
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
}