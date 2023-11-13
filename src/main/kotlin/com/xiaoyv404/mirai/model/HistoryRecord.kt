package com.xiaoyv404.mirai.model

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDateTime

interface HistoryRecord : Entity<HistoryRecord> {
    companion object : Entity.Factory<HistoryRecord>()

    val id: Long
    var sendTime: LocalDateTime
    var command: String
    var gid: Long
    var uid: Long
    var msgId: Long
    var content: String
}

object HistoryRecords : Table<HistoryRecord>("History_Record") {
    val id = long("id").primaryKey().bindTo { it.id }
    val sendTime = datetime("send_time").bindTo { it.sendTime }
    val eventType = varchar("command").bindTo { it.command }
    val groupId = long("group_id").bindTo { it.gid }
    val senderId = long("sender_id").bindTo { it.uid }
    val msgId = long("msg_id").bindTo { it.msgId }
    val content = text("content").bindTo { it.content }
}