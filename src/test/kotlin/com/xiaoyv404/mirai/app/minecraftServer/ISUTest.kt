package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.BaseTest
import net.mamoe.mirai.LowLevelApi
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.events.GroupMessagePostSendEvent
import net.mamoe.mirai.mock.utils.simpleMemberInfo
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ISUTest : BaseTest() {

    @OptIn(LowLevelApi::class)
    @Test
    fun testPlayerStatusCommand() = runTest {
        runAndReceiveEventBroadcast {
            val mockMember = bot.addGroup(2020, "testGroup")
                .addMember(simpleMemberInfo(2021, "test", permission = MemberPermission.MEMBER))
            mockMember.says {
                +"404 玩家状态 test1"
            }
            mockMember.says {
                +"404 玩家状态"
            }
            mockMember.says {
                +"404 玩家状态 tEsT"
            }
        }.runIFsApp { args, msg -> ISU().executeRsh(args, msg) }
            .filterIsInstance<GroupMessagePostSendEvent>().let { msg ->
                assertEquals(
                    "无数据",
                    msg.getOrNull(0)?.message?.contentToString()
                )
                assertEquals(
                    """
                        名字: Test
                        不在线
                        最后在线时间: 2006-04-16T06:58:39.810
                        服务器: Test
                        UUID: test
                        身份: 毛玉
                    """.trimIndent(),
                    msg.getOrNull(1)?.message?.contentToString()
                )
                assertEquals(
                    """
                        名字: Test
                        不在线
                        最后在线时间: 2006-04-16T06:58:39.810
                        服务器: Test
                        UUID: test
                        身份: 毛玉
                    """.trimIndent(),
                    msg.getOrNull(2)?.message?.contentToString()
                )
            }
    }
}