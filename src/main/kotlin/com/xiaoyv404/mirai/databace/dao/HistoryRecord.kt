package com.xiaoyv404.mirai.databace.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDateTime

interface HistoryRecord : Entity<HistoryRecord> {
    val id: Long
    val sendTime: LocalDateTime
    val eventType: String
    val groupId: Long
    val groupName: String
    val senderId: Long
    val senderName: String
    val msgId: Long
    val content: String
}

object HistoryRecords : Table<HistoryRecord>("History_Record") {
    val id = long("id").primaryKey()
    val sendTime = datetime("send_time")
    val eventType = varchar("event_type")
    val groupId = long("group_id")
    val groupName = varchar("group_name")
    val senderId = long("sender_id")
    val senderName = varchar("sender_name")
    val msgId = long("msg_id")
    val content = text("content")
}