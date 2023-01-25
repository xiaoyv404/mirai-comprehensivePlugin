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
        if(requester.queryProfile().qLevel > 64)
            event.accept()
    }
}