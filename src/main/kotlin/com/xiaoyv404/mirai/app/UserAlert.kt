package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
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
class UserAlert : NfApp(), IFshApp {
    override fun getAppName() = "UserAlert"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "用户警告相关"
    override fun getCommands() = arrayOf("警告")
    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        if (msg.subject !is Group)
            return false
        val sender = msg.sender as Member
        if (msg.isNotAdmin() || !sender.permission.isOperator())
            return false

        val at: At? by msg.message.orNull()

        if (at == null)
            return false

        val user = User {
            this.id = at!!.target
        }.findById() ?: User {
            this.id = at!!.target
        }
        user.warningTimes++

        user.save()

        msg.reply("已警告，本次为第${user.warningTimes}次警告")

        return true
    }
}