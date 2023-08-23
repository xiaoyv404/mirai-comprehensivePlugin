package com.xiaoyv404.mirai.core

import com.xiaoyv404.mirai.database.Database
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.MiraiLogger
import java.io.InputStream

object MessageProcessor {
    private val log = MiraiLogger.Factory.create(MessageProcessor::class)

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

    suspend fun MessageEvent.reply(msg: Message, quote: Boolean = false): MessageReceipt<Contact> {
        return this.reply(msg.toMessageChain(), quote)
    }

    suspend fun MessageEvent.reply(msg: String, quote: Boolean = false): MessageReceipt<Contact> {
        return this.reply(PlainText(msg), quote)
    }
}