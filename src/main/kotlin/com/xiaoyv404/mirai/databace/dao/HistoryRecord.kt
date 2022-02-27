package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.databace.Database.db
import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.LocalDateTime

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

private val Database.historyRecord get() = this.sequenceOf(HistoryRecords)

fun HistoryRecord.save(){
    db.historyRecord.add(this)
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