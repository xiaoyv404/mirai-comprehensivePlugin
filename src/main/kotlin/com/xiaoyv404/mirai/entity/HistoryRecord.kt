package com.xiaoyv404.mirai.entity

import org.ktorm.entity.*
import org.ktorm.schema.*
import java.time.*

interface HistoryRecord : Entity<HistoryRecord> {
    companion object : Entity.Factory<HistoryRecord>()
    val id: Long
    var sendTime: LocalDateTime
    var eventType: String
    var groupId: Long
    var groupName: String
    var senderId: Long
    var senderName: String
    var msgId: Long
    var content: String
}

object HistoryRecords : Table<HistoryRecord>("History_Record") {
    val id = long("id").primaryKey().bindTo { it.id }
    val sendTime = datetime("send_time").bindTo { it.sendTime }
    val eventType = varchar("event_type").bindTo { it.eventType }
    val groupId = long("group_id").bindTo { it.groupId }
    val groupName = varchar("group_name").bindTo { it.groupName }
    val senderId = long("sender_id").bindTo { it.senderId }
    val senderName = varchar("sender_name").bindTo { it.senderName }
    val msgId = long("msg_id").bindTo { it.msgId }
    val content = text("content").bindTo { it.content }
}