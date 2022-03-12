package com.xiaoyv404.mirai.core

import net.mamoe.mirai.event.events.MessageEvent

abstract class NfAppMessageHandler : NfApp() {

    abstract suspend fun handleMessage(msg: MessageEvent)

}