package com.xiaoyv404.mirai.service

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.service.accessControl.authorityIdentification
import io.ktor.util.*
import net.mamoe.mirai.contact.remarkOrNameCardOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.MiraiInternalApi
import kotlin.coroutines.EmptyCoroutineContext

var BroadcastStatus = false

@KtorExperimentalAPI
@MiraiInternalApi
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
            if ((getUserInformation(sender.id).bot != true) && authorityIdentification(
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
            if (rd[3]!!.value == "-h" || rd[3]!!.value == "--help") {
                group.sendMessage("help")
            } else {
                val idL = rd[3]!!.value.replace("@", "").toLong()
                val status: Boolean? = getUserInformation(idL).bot
                if (status != null) {
                    updateUserBot(idL, !status)
                } else {
                    createUserInformation(idL, false, bot = true, setu = false)
                }
                group.sendMessage("Done")
            }
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
        case("test") {
            println(PluginMain.dataFolderPath)
        }
    }
    GlobalEventChannel.subscribeFriendMessages {
        matching(Command.ban) {
            if (getUserInformation(sender.id).admin == true) {
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



        always {
            if (getUserInformation(sender.id).admin == true) {
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
                                        if (groupNoticeSwitchRead(it.id, "AdminBroadcast")) {
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