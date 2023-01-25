package com.xiaoyv404.mirai.app.groupHelper

import com.xiaoyv404.mirai.core.*
import net.mamoe.mirai.event.events.*

abstract class NfAppMemberJoinRequestHandler : NfApp() {
    abstract suspend fun handleMessage(event: MemberJoinRequestEvent)
}