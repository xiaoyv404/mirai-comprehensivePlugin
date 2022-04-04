package com.xiaoyv404.mirai.core

import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.OnlineMessageSource

fun MessageEvent.uid(): Long {
    return sender.id
}

fun MessageEvent.gid(): Long {
    return if (source is OnlineMessageSource.Incoming.FromGroup) {
        (source as OnlineMessageSource.Incoming.FromGroup).group.id
    } else {
        0L
    }
}
