package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.BaseTest
import kotlinx.coroutines.flow.toList
import net.mamoe.mirai.LowLevelApi
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.OnlineMessageSource
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.source
import net.mamoe.mirai.mock.utils.simpleMemberInfo
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class UserAlertTest : BaseTest() {
    @OptIn(LowLevelApi::class)
    @Test
    internal fun testUserAlertWarn() = runTest {
        val mockGroup = bot.addGroup(1147939635, "MC幻想乡-旧约酒馆")
        val mockMember = mockGroup
            .addMember(simpleMemberInfo(5971, "test", permission = MemberPermission.OWNER))

        mockMember.says(buildMessageChain {
            +"警告"
            +At(1111)
        })
        GlobalEventChannel.subscribeOnce<Event> {
            assertIsInstance<GroupMessageEvent>(this) {
                UserAlert().handleMessage(this)
                assertIsInstance<OnlineMessageSource.Incoming.FromGroup>(message.source)
            }
        }
        mockGroup.roamingMessages.getAllMessages().toList().let { msg ->
            assertEquals("警告@1111", msg[0].contentToString())
        }
    }
}
