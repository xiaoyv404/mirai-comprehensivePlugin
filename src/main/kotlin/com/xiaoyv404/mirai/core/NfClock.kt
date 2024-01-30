package com.xiaoyv404.mirai.core

import java.time.Clock
import java.time.LocalDateTime

object NfClock {
    private var clock: Clock? = null
    fun mockTime(clock: Clock) {
        this.clock = clock
    }

    fun now(): LocalDateTime {
        return if (clock != null)
            LocalDateTime.now(clock)
        else
            LocalDateTime.now()
    }
}