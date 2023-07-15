package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.BaseTest
import net.mamoe.mirai.LowLevelApi
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.events.GroupMessagePostSendEvent
import net.mamoe.mirai.mock.utils.simpleMemberInfo
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


internal class DebuMeTest : BaseTest() {
    @OptIn(LowLevelApi::class)
    @Test

    internal fun testDebuMeCommand() = runTest {
        runAndReceiveEventBroadcast {
            val mockMember = bot.addGroup(2020, "testGroup")
                .addMember(simpleMemberInfo(2021, "test", permission = MemberPermission.MEMBER))
            mockMember.says {
                +"~me"
            }
        }.runIFsApp { array, msg ->
            DebuMe().executeRsh(array, msg)
        }.filterIsInstance<GroupMessagePostSendEvent>().let { msg ->
            assertEquals("", msg[0].message.contentToString())
        }
    }
}