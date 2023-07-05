package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfAppMessageHandler
import com.xiaoyv404.mirai.dao.findById
import com.xiaoyv404.mirai.dao.groupType
import com.xiaoyv404.mirai.dao.isNotAdmin
import com.xiaoyv404.mirai.dao.save
import com.xiaoyv404.mirai.model.GroupType
import com.xiaoyv404.mirai.model.User
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain

@App
class UserAlert : NfAppMessageHandler() {
    override fun getAppName() = "UserAlert"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "用户警告相关"
    override suspend fun handleMessage(msg: MessageEvent) {
        if (!msg.message.contentToString().startsWith("警告"))
            return
        if (msg.groupType() != GroupType.MCG)
            return
        val sender = msg.sender as Member
        if (msg.isNotAdmin() && !sender.permission.isOperator())
            return

        val ats = msg.message.filterIsInstance<At>()
        if (ats.isEmpty())
            return

        val str = MessageChainBuilder()
        ats.forEachIndexed { k, v ->
            val user = User {
                this.id = v.target
            }.findById() ?: User {
                this.id = v.target
            }
            user.warningTimes++

            user.save()
            str.append(buildMessageChain {
                +"已警告"
                +v
                +"，本次为第${user.warningTimes}次警告"
                if (k != ats.size - 1)
                    +PlainText("\n")
            })
        }

        msg.reply(str.build())

        return
    }
}
