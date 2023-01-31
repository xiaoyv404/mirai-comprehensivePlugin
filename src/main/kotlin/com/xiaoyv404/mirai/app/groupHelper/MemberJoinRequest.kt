package com.xiaoyv404.mirai.app.groupHelper

import com.xiaoyv404.mirai.core.*
import net.mamoe.mirai.*
import net.mamoe.mirai.event.events.*

@App
class MemberJoinRequest : NfAppMemberJoinRequestHandler() {
    override fun getAppName() = "群申请自动审批功能"
    override fun getVersion() = "1.0.0"
    override suspend fun handleMessage(event: MemberJoinRequestEvent) {
        if (event.groupId != 705664551L)
            return

        val profile = Mirai.queryProfile(event.bot, event.fromId)
        if(profile.qLevel >= 32)
            event.accept()
    }
}