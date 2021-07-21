package com.xiaoyv404.mirai.service.history

import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.HistoryRecords
import org.ktorm.dsl.insert
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun insertMessage(
    sendTimestamp: Int,
    eventType: String,
    groupId: Long,
    groupName: String,
    senderId: Long,
    senderName: String,
    msgId: Long,
    content: String
) {
    Database.db
        .insert(HistoryRecords) {
            set(
                it.sendTime,
                LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(sendTimestamp.toLong() + 10800), ZoneId.ofOffset(
                        "UTC",
                        ZoneOffset.of("+18")
                    )
                )
            )
            set(it.eventType, eventType)
            set(it.groupId, groupId)
            set(it.groupName, groupName)
            set(it.senderId, senderId)
            set(it.senderName, senderName)
            set(it.msgId, msgId)
            set(it.content, content)
        }
}




