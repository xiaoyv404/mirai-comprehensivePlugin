package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.databace.dao.*
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.remarkOrNameCardOrNick
import net.mamoe.mirai.contact.remarkOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.nextMessage
import kotlin.coroutines.EmptyCoroutineContext

@App
class SomeThing : NfApp(), IFshApp {
    override fun getAppName() = "SomeThing"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "�����Ӱ˵Ķ���"
    override fun getCommands(): Array<String> = arrayOf("~me")

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        if (args[1] == "~me") {
            debuMe(args.getOrNull(2), msg)
        }
        return true
    }

    private suspend fun debuMe(data: String?, msg: MessageEvent) {
        val subject = msg.subject
        val sender = msg.sender
        if (sender.isNotBot() && authorityIdentification(
                sender.id,
                subject.id,
                "DebuMe"
            )
        ) {
            val name = data ?: if (sender is Member) {
                sender.remarkOrNameCardOrNick
            } else
                sender.remarkOrNick
            subject.sendMessage("*${name}���ڵ��Ͽ���˵����������${name}ʲôʱ����д����ǰٷ�֮һ����ѽ������")
        }
    }

    private var broadcastStatus = false

    override fun init() {
        GlobalEventChannel.subscribeAlways(
            BotInvitedJoinGroupRequestEvent::class,
            EmptyCoroutineContext
        ) {
            bot.getFriend(2083664136L)!!
                .sendMessage(
                    "�¼�ID: ${it.eventId}\n" +
                        "����,${it.invitorNick}(${it.invitorId})�����Ҽ���Ⱥ${it.groupName}(${it.groupId})"
                )
            accept()
        }

        GlobalEventChannel.subscribeGroupMessages {
            case("404 help"){
                group.sendMessage("https://www.xiaoyv404.top/archives/404.html")
            }
            case("404 status") {
                group.sendMessage(
                    "Bot: ${bot.nick}(${bot.id})\n" +
                        "status: Online "
                )
            }
            finding(Command.addBot) {
                val rd = it.groups
                val idL = rd[4]!!.value.toLong()
                User{
                    id = idL
                    bot = true
                }.save()
                group.sendMessage("��ӳɹ�~")
            }

            at(2083664136L).invoke {
                var chain = buildMessageChain {
                    +PlainText("${sender.nick}(${sender.id})��${group.name}(${group.id})�ж�����˵��\n")
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
                    +PlainText("${sender.nick}(${sender.id})��${group.name}(${group.id})�жԼ���˵��\n")
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
        GlobalEventChannel.subscribeFriendMessages {
            matching(Command.ban) {
                if (sender.isAdmin()) {
                    val rd = it.groups
                    when {
                        (rd[3]!!.value == "-h") or (rd[3]!!.value == "--help") -> friend.sendMessage("help")
                        rd[7]!!.value == "unban"                               -> {
                            try {
                                bot
                                    .getGroupOrFail(rd[5]!!.value.toLong())
                                    .getOrFail(rd[6]!!.value.toLong())
                                    .unmute()
                            } catch (e: NoSuchElementException) {
                                friend.sendMessage("��Ч��Ⱥ���Ա")
                            }
                        }
                        else                                                   -> {
                            try {
                                bot
                                    .getGroupOrFail(rd[5]!!.value.toLong())
                                    .getOrFail(rd[6]!!.value.toLong())
                                    .mute(rd[8]!!.value.toInt())
                            } catch (e: NoSuchElementException) {
                                friend.sendMessage("��Ч��Ⱥ���Ա")
                            }
                        }
                    }
                }
            }
            matching(Command.join) {}

            matching(Regex("404 sendto")) {
                if (sender.isAdmin()) {
                    subject.sendMessage("�뷢��Ⱥid")
                    val gp = nextMessage().contentToString().split("\n")
                    PluginMain.logger.info("Ⱥ�ĸ���${gp.size}")
                    subject.sendMessage("�뷢��msg")
                    val msg = nextMessage()
                    gp.forEach {
                        val gpL = it.toLong()
                        bot.getGroup(gpL)?.sendMessage(msg)
                    }
                }
            }

            always {
                if (sender.isAdmin()) {
                    when (message.contentToString()) {
                        "!!����ȫ��㲥" -> {
                            broadcastStatus = !broadcastStatus
                            friend.sendMessage(
                                "ȫ��㲥��" +
                                    if (broadcastStatus)
                                        "����"
                                    else
                                        "�ر�"
                            )
                        }
                        else       -> {
                            when {
                                broadcastStatus -> {
                                    val entryMassage = message.serializeToMiraiCode()
                                    if (entryMassage != "") {
                                        bot.groups.forEach {
                                            val status = Group {
                                                id = it.id
                                            }.noticeSwitchRead("AdminBroadcast")
                                            if (status) {
                                                bot.getGroup(it.id)
                                                    ?.sendMessage(
                                                        MiraiCode.deserializeMiraiCode(entryMassage)
                                                    )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }    }
}