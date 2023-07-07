package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.BaseTest
import net.mamoe.mirai.LowLevelApi
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.events.GroupMessagePostSendEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.mock.utils.simpleMemberInfo
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class UserAlertTest : BaseTest() {
    @OptIn(LowLevelApi::class)
    @Test
    internal fun testUserAlertWarn() = runTest {
        runAndReceiveEventBroadcast {
            val mockMember = bot.addGroup(1147939635, "MC幻想乡-旧约酒馆")
                .addMember(simpleMemberInfo(5971, "test", permission = MemberPermission.OWNER))
            mockMember.says {
                +"警告"
                +At(1111)
            }
            mockMember.says {
                +"警告一次"
                +At(1111)
                +At(1008611)
                +At(464)
            }
        }.filterIsInstance<MessageEvent>().runNfMessageEventApp {
            UserAlert().handleMessage(this)
        }.filterIsInstance<GroupMessagePostSendEvent>().let { msg ->
            assertEquals("已警告@1111，本次为第1次警告", msg[0].message.contentToString())
            assertEquals(
                """
                已警告@1111，本次为第2次警告
                已警告@1008611，本次为第1次警告
                已警告@464，本次为第1次警告
            """.trimIndent(), msg[1].message.contentToString()
            )
        }
    }
}
