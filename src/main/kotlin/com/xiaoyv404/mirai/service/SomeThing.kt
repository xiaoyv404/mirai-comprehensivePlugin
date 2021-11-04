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
                "事件ID: ${it.eventId}\n" +
                    "主人,${it.invitorNick}(${it.invitorId})邀请我加入群${it.groupName}(${it.groupId})"
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
        case("BiliBili解析功能") {
            group.sendMessage(
                "w, BiliBili解析功能现在是" +
                    if (authorityIdentification(0L, group.id, "BiliBiliParsing"))
                        "开"
                    else "关"
                        + "的哦"
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
                group.sendMessage("*${name}坐在地上哭着说道「可怜哒${name}什么时候才有大佬们百分之一厉害呀……」")
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
                            friend.sendMessage("无效的群或成员")
                        }
                    }
                    else                                                   -> {
                        try {
                            bot
                                .getGroupOrFail(rd[5]!!.value.toLong())
                                .getOrFail(rd[6]!!.value.toLong())
                                .mute(rd[8]!!.value.toInt())
                        } catch (e: NoSuchElementException) {
                            friend.sendMessage("无效的群或成员")
                        }
                    }
                }
            }
        }
        matching(Command.join) {}



        always {
            if (getUserInformation(sender.id).admin == true) {
                when (message.contentToString()) {
                    "!!开关全体广播" -> {
                        BroadcastStatus = !BroadcastStatus
                        friend.sendMessage(
                            "全体广播已" +
                                if (BroadcastStatus)
                                    "开启"
                                else
                                    "关闭"
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