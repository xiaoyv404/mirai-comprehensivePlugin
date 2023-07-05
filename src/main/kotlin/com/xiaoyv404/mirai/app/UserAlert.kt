package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfAppMessageHandler
import com.xiaoyv404.mirai.dao.findById
import com.xiaoyv404.mirai.dao.isNotAdmin
import com.xiaoyv404.mirai.dao.save
import com.xiaoyv404.mirai.model.User
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.orNull

@App
class UserAlert : NfAppMessageHandler() {
    override fun getAppName() = "UserAlert"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "用户警告相关"
    override suspend fun handleMessage(msg: MessageEvent) {
        if (!msg.message.contentToString().startsWith("警告"))
            return
        if (msg.subject !is Group)
            return
        val sender = msg.sender as Member
        if (msg.isNotAdmin() && !sender.permission.isOperator())
            return

        val at: At? by msg.message.orNull()

        if (at == null)
            return

        val user = User {
            this.id = at!!.target
        }.findById() ?: User {
            this.id = at!!.target
        }
        user.warningTimes++

        user.save()

        msg.reply("已警告，本次为第${user.warningTimes}次警告")

        return
    }
}