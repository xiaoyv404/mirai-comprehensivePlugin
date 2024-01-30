package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.BaseTest
import com.xiaoyv404.mirai.PluginData
import com.xiaoyv404.mirai.core.NfClock
import net.mamoe.mirai.event.events.FriendMessagePostSendEvent
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlin.test.assertEquals

internal class AdminToolsTest : BaseTest() {

    @Test
    internal fun testCommandDebug() = runTest {
        val clock = Clock.fixed(Instant.parse("2006-04-16T06:59:39.00Z"), ZoneId.of("UTC"))
        NfClock.mockTime(clock)
        assertEquals(PluginData.deBug, false)
        runAndReceiveEventBroadcast {
            val mockFriend = bot.addFriend(123, "test")
            mockFriend.says {
                +"404 debug true"
            }
        }.runIFsApp(AdminTools())
            .filterIsInstance<FriendMessagePostSendEvent>().let { msg ->
                assertEquals("Debug模式已切换至 true", msg.getOrNull(0)?.message?.contentToString())
            }
        assertEquals(PluginData.deBug, true)
        runAndReceiveEventBroadcast {
            val mockFriend = bot.addFriend(123, "test")
            mockFriend.says {
                +"404 debug false"
            }
        }.runIFsApp(AdminTools())
            .filterIsInstance<FriendMessagePostSendEvent>().let { msg ->
                assertEquals("Debug模式已切换至 false", msg.getOrNull(0)?.message?.contentToString())
            }
        assertEquals(PluginData.deBug, false)
    }
}