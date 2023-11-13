package com.xiaoyv404.mirai.app.fsh

import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.gid
import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.dao.save
import com.xiaoyv404.mirai.model.HistoryRecord
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.ids
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@App
object CommandHistory {
    fun add(command: String, msg: MessageEvent) {
        HistoryRecord {
            this.sendTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(msg.time.toLong() + 10800), ZoneId.ofOffset(
                    "UTC",
                    ZoneOffset.of("+18")
                )
            )
            this.command = command
            this.gid = msg.gid()
            this.uid = msg.uid()
            this.msgId = msg.message.ids[0].toLong()
            this.content = msg.message.contentToString()
        }.save()
    }
}