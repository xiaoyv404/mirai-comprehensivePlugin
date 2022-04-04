package com.xiaoyv404.mirai.core

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.*

object MessageProcessor {

    suspend fun reply(src: MessageEvent, msg: MessageChain, quote: Boolean): MessageReceipt<Contact> {
        val contact = src.subject
        val toSend = if (quote && (contact is Group)) QuoteReply(src.source).plus(msg) else msg
        val sent = contact.sendMessage(toSend)
//        markSent(src, sent)
        return sent
    }

    suspend fun reply(src: MessageEvent, msg: Message, quote: Boolean): MessageReceipt<Contact> {
        return reply(src, msg.toMessageChain(), quote)
    }

    suspend fun MessageEvent.reply(msg: String, quote: Boolean = false): MessageReceipt<Contact> {
        return reply(this, PlainText(msg), quote)
    }
}