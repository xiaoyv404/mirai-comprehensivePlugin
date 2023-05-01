package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.dao.*
import com.xiaoyv404.mirai.model.User
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.*
import net.mamoe.mirai.message.code.*
import net.mamoe.mirai.utils.*
import org.apache.commons.cli.*

@App
class AdminTools : NfApp(), IFshApp {
    override fun getAppName() = "AdminTools"
    override fun getVersion() = "1.0.3"
    override fun getAppDescription() = "管理员管理工具"
    override fun getCommands() = arrayOf("-sendto", "-ban", "!!开启全体广播", "-bot", "-accept", "-debug", "-admin")

    private val banOptions = Options().apply {
        addOption("g", "group", true, "群聊ID")
        addOption("u", "unBan", false, "取消禁言")
        addOption("t", "time", true, "禁言时间")
    }

    private val adminOptions = Options().apply {
        addOption("r", "remove", false, "取消此成员的管理")
        addOption("u", "uid", true, "成员ID")
    }

    private val eventList get() = PluginData.eventMap

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        if (msg.isNotAdmin())
            return false

        when (args[0]) {
            "-sendto" -> sendto(msg)
            "-bot" -> {
                if (args[1] == "add")
                    addBot(args.getOrNull(2) ?: return false, msg)
            }
            "-ban" -> {
                ban((args.getOrNull(1) ?: return false).toLong(), IFshApp.cmdLine(banOptions, args), msg)
            }
            "!!开启全体广播" -> adminBroadcast(msg)
            "-accept" -> accept(msg, args.getOrNull(1)?.toLongOrNull() ?: return false)
            "-debug" -> debug(msg, args.getOrNull(1)?.toBooleanStrictOrNull() ?: return false)
            "-admin" ->
                groupPermission(
                    msg,
                    IFshApp.cmdLine(adminOptions, args)
                )
        }
        return true
    }

    private suspend fun debug(msg: MessageEvent, switch: Boolean) {
        PluginData.deBug = switch
        msg.reply("Debug模式已切换至 $switch", true)
    }

    private suspend fun groupPermission(msg: MessageEvent, data: CommandLine) {
        if (msg.subject !is Group)
            return

        val subject = msg.subject as Group
        if (!subject.botPermission.isOwner()) {
            msg.reply("bot权限不足", true)
            return
        }

        val uid = if (data.hasOption("u"))
            data.getOptionValue("u").toLong()
        else
            msg.uid()


        val member = subject.getMember(uid)
        if (member == null) {
            msg.reply("查无此人", true)
            return
        }

        member.modifyAdmin(!data.hasOption("r"))
        return
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
        val idA = (Regex("\\d+").find(data) ?: return).value.toLong()
        User {
            id = idA
            bot = true
        }.save()
        msg.reply("添加成功~")

    }

    private suspend fun ban(uid: Long, data: CommandLine, msg: MessageEvent) {
        val gid = if (data.hasOption("group"))
            data.getOptionValue("group").toLong()
        else if (msg.gid() != 0L)
            msg.gid()
        else {
            msg.reply("缺少参数: groupId")
            return
        }

        if (uid == msg.bot.id) {
            msg.reply("ban自己是哒咩的！")
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
                        val status = com.xiaoyv404.mirai.model.Group {
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