package com.xiaoyv404.mirai.core

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Database
import io.lettuce.core.SetArgs
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.recallMessage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.*

object MessageProcessor {
    private val log = PluginMain.logger

    @OptIn(DelicateCoroutinesApi::class)
    fun init() {
        GlobalEventChannel.subscribeAlways(MessageRecallEvent::class.java) { onRecall(it) }
    }


    @DelicateCoroutinesApi
    private fun onRecall(event: MessageRecallEvent) {
        val bot = event.bot
        Database.rdb.sync().keys("fmp:replied:${event.rgwMsgIdentity()}:*").forEach { key ->
            val sentIdentity = Database.rdb.sync().get(key)
            if (sentIdentity != null) {
                val split = sentIdentity.split("#")
                if (split.size == 5) {
                    val ids = ArrayList<Int>()
                    split[0].split(",").forEach { ids.add(it.toInt()) }
                    val internalIds = ArrayList<Int>()
                    split[1].split(",").forEach { internalIds.add(it.toInt()) }
                    val time = split[2].toInt()

                    val recallSource = MessageSourceBuilder()
                    recallSource.ids = ids.toIntArray()
                    recallSource.internalIds = internalIds.toIntArray()
                    recallSource.time = time
                    recallSource.fromId = split[3].toLong()
                    recallSource.targetId = split[4].toLong()

                    log.info("撤回触发过的消息$sentIdentity")
                    GlobalScope.launch {
                        if (event is MessageRecallEvent.GroupRecall) {
                            event.group.recallMessage(recallSource.build(bot.id, MessageSourceKind.GROUP))
                        } else if (event is MessageRecallEvent.FriendRecall) {
                            event.operator.recallMessage(recallSource.build(bot.id, MessageSourceKind.FRIEND))
                        }
                    }
                }
            }
        }
    }

    private fun markSent(src: MessageEvent, sent: MessageReceipt<Contact>) {
        val originIdentity = src.source.rgwMsgIdentity()
        val sentIdentity = "${sent.source.rgwMsgIdentity()}#${sent.source.fromId}#${sent.source.targetId}"
        log.info("触发原消息标识$originIdentity  发送消息标识$sentIdentity")
        val redisKey = "fmp:replied:$originIdentity:$sentIdentity"

        val setArgs = SetArgs.Builder.nx().ex(180)
        Database.rdb.async().set(redisKey, sentIdentity, setArgs)
    }

    suspend fun reply(src: MessageEvent, msg: MessageChain, quote: Boolean): MessageReceipt<Contact> {
        val contact = src.subject
        val toSend = if (quote && (contact is Group)) QuoteReply(src.source).plus(msg) else msg
        val sent = contact.sendMessage(toSend)
        markSent(src, sent)
        return sent
    }

    suspend fun reply(src: MessageEvent, msg: Message, quote: Boolean): MessageReceipt<Contact> {
        return reply(src, msg.toMessageChain(), quote)
    }

    suspend fun MessageEvent.reply(msg: String, quote: Boolean = false): MessageReceipt<Contact> {
        return reply(this, PlainText(msg), quote)
    }
}