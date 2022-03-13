package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.app.accessControl.authorityIdentification
import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.databace.dao.*
import net.mamoe.mirai.contact.remarkOrNameCardOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.nextMessage
import kotlin.coroutines.EmptyCoroutineContext

var BroadcastStatus = false

fun someThinkEntrance() {
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
        case("BiliBili��������") {
            group.sendMessage(
                "w, BiliBili��������������" +
                    if (authorityIdentification(0L, group.id, "BiliBiliParsing"))
                        "��"
                    else "��"
                        + "��Ŷ"
            )
        }
        matching(Command.debuMe) {
            if (sender.isNotBot() && authorityIdentification(
                    sender.id,
                    group.id,
                    "DebuMe"
                )
            ) {
                val rd = it.groups
                val name = if (rd[2]!!.value == "")
                    sender.remarkOrNameCardOrNick
                else
                    rd[2]!!.value
                group.sendMessage("*${name}���ڵ��Ͽ���˵����������${name}ʲôʱ����д����ǰٷ�֮һ����ѽ������")
            }
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
                        BroadcastStatus = !BroadcastStatus
                        friend.sendMessage(
                            "ȫ��㲥��" +
                                if (BroadcastStatus)
                                    "����"
                                else
                                    "�ر�"
                        )
                    }
                    else       -> {
                        when {
                            BroadcastStatus -> {
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
    }
}