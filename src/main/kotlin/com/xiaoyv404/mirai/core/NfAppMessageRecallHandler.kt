package com.xiaoyv404.mirai.core

import net.mamoe.mirai.event.events.MessageRecallEvent

abstract class NfAppMessageRecallHandler:NfApp(){

    abstract suspend fun handleMessage(event: MessageRecallEvent)

}