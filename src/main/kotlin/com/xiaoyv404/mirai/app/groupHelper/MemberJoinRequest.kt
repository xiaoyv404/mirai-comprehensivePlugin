package com.xiaoyv404.mirai.app.groupHelper

import com.xiaoyv404.mirai.core.*
import net.mamoe.mirai.event.events.*

@App
class MemberJoinRequest : NfAppMemberJoinRequestHandler() {
    override fun getAppName() = "群申请自动审批功能"
    override fun getVersion() = "1.0.0"
    override suspend fun handleMessage(event: MemberJoinRequestEvent) {
        println("1111")
        if (event.groupId != 705664551L)
            return
        println(222222)

        val requester = event.bot.getStranger(event.fromId)?: return
        println(11111111)
        if(requester.queryProfile().qLevel >= 32) {
            event.accept()
            event.bot.getFriend(2083664136)!!.sendMessage("通过 ${event.fromNick}的群申请")
        }else
            event.bot.getFriend(2083664136)!!.sendMessage(requester.queryProfile().qLevel.toString())
    }
}