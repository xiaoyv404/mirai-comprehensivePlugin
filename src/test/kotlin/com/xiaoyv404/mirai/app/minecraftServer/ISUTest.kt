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
            val mockGroup = bot.addGroup(2020, "testGroup")
            mockGroup.addMember(simpleMemberInfo(2021, "test", permission = MemberPermission.MEMBER)).apply {
                says {
                    +"404 玩家状态 test1"
                }
                says {
                    +"404 玩家状态"
                }
                says {
                    +"404 玩家状态 tEsT"
                }
            }
            mockGroup.addMember(simpleMemberInfo(2050, "Test2", permission = MemberPermission.MEMBER)).says {
                +"404 玩家状态"
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
                assertEquals(
                    "敲，有人在假冒Test2",
                    msg.getOrNull(3)?.message?.contentToString()
                )
            }
    }
}