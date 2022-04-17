package com.xiaoyv404.mirai.app.someThing

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
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.nextMessage
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Options

@App
class AdminTools  : NfApp(), IFshApp {
    override fun getAppName() = "AdminTools"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "����Ա������"
    override fun getCommands() = arrayOf("-sendto","-ban", "!!����ȫ��㲥", "-bot")

    private val banOptions = Options().apply {
        addOption("g", "group", true, "Ⱥ��ID")
        addOption("u", "unBan", false, "ȡ������")
        addOption("t", "time", true, "����ʱ��")
    }

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
            "!!����ȫ��㲥" -> adminBroadcast(msg)
        }
        return true
    }

    private suspend fun sendto(msg: MessageEvent) {
        if (msg.isNotAdmin())
            return

        msg.reply("�뷢��Ⱥid")
        val gpIds = Regex("\\d+").findAll(msg.nextMessage().contentToString()).toList()
        log.info("Ⱥ�ĸ���${gpIds.size}")
        msg.reply("�뷢��msg")
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
        msg.reply("��ӳɹ�~")

    }

    private suspend fun ban(uid: Long, data: CommandLine, msg: MessageEvent) {
        if (msg.isNotAdmin())
            return

        val gid = if (data.hasOption("group"))
            data.getOptionValue("group").toLong()
        else if (msg.gid() != 0L)
            msg.gid()
        else {
            msg.reply("ȱ�ٲ���: groupId")
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
                msg.reply("404û��Ȩ��qwq")
                return
            }
            group.getOrFail(uid)
        } catch (e: NoSuchElementException) {
            msg.reply("��Ч��Ⱥ���Ա")
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

        msg.reply("ȫ��㲥�ѿ���")
        GlobalEventChannel.subscribe<FriendMessageEvent> {
            if (msg.uid() == it.sender.id) {
                val entryMassage = it.message.serializeToMiraiCode()
                if (entryMassage == "!!�ر�ȫ��㲥") {
                    msg.reply("ȫ��㲥�ѹر�")
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