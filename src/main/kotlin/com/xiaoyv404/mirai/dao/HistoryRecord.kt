package com.xiaoyv404.mirai.dao

import com.xiaoyv404.mirai.databace.*
import com.xiaoyv404.mirai.entity.*
import org.ktorm.entity.*

private val org.ktorm.database.Database.historyRecord get() = this.sequenceOf(HistoryRecords)

fun HistoryRecord.save(){
    Database.db.historyRecord.add(this)
}