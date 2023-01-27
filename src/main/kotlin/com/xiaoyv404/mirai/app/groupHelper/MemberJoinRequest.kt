package com.xiaoyv404.mirai.app.groupHelper

import com.xiaoyv404.mirai.core.*
import net.mamoe.mirai.event.events.*

@App
class MemberJoinRequest : NfAppMemberJoinRequestHandler() {
    override fun getAppName() = "群申请自动审批功能"
    override fun getVersion() = "1.0.0"
    override suspend fun handleMessage(event: MemberJoinRequestEvent) {
        if (event.groupId != 705664551L)
            return

        val requester = event.bot.getStranger(event.fromId)?: return
        if(requester.queryProfile().qLevel >= 32) {
            event.accept()
            event.bot.getFriend(2083664136)!!.sendMessage("通过 ${event.fromNick}的群申请")
        }
    }
}