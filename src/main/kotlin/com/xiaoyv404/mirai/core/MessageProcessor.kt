package com.xiaoyv404.mirai.core

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Database
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.recallMessage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.*
import java.io.InputStream
import java.util.concurrent.TimeUnit

object MessageProcessor {
    private val log = PluginMain.logger

    @OptIn(DelicateCoroutinesApi::class)
    fun init() {
        GlobalEventChannel.subscribeAlways(MessageRecallEvent::class.java) { onRecall(it) }
    }


    @DelicateCoroutinesApi
    private fun onRecall(event: MessageRecallEvent) {
        val bot = event.bot
        Database.rdb.keys("fmp:replied:${event.rgwMsgIdentity()}:*").get(1, TimeUnit.MINUTES).forEach { key ->
            val sentIdentity = Database.rdb.get(key).get(1,TimeUnit.MINUTES)
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

        Database.rdb.setex(redisKey,1800L, sentIdentity)
    }

    private suspend fun replyImg(src: MessageEvent, input: InputStream, type: String?): MessageReceipt<Contact> {
        val sent = src.subject.sendImage(input, type)
        markSent(src, sent)
        return sent
    }

    suspend fun MessageEvent.replayImg(input: InputStream, type: String?): MessageReceipt<Contact> {
        return replyImg(this, input, type)
    }

    suspend fun MessageEvent.reply(msg: MessageChain, quote: Boolean): MessageReceipt<Contact> {
        val contact = this.subject
        val toSend = if (quote && (contact is Group)) QuoteReply(this.source).plus(msg) else msg
        val sent = contact.sendMessage(toSend)
        markSent(this, sent)
        return sent
    }

    suspend fun MessageEvent.reply(msg: Message, quote: Boolean): MessageReceipt<Contact> {
        return this.reply( msg.toMessageChain(), quote)
    }

    suspend fun MessageEvent.reply(msg: String, quote: Boolean = false): MessageReceipt<Contact> {
        return this.reply(PlainText(msg), quote)
    }
}