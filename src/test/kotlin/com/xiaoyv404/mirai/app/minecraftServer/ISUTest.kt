package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.BaseTest
import com.xiaoyv404.mirai.PluginConfig
import net.mamoe.mirai.LowLevelApi
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.events.GroupMessagePostSendEvent
import net.mamoe.mirai.mock.utils.simpleMemberInfo
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneId
import kotlin.test.assertEquals

internal class ISUTest : BaseTest() {
    @OptIn(LowLevelApi::class)
    @Test
    fun testPlayerStatusCommand() = runTest {
        PluginConfig.etc.planApiUrl = "http://127.0.0.1:${mockWebServer.port}/"
        mockWebServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody("{\"kick_count\":26,\"mob_kill_count\":87419,\"player_kill_count\":178,\"registered\":1612772032858,\"uuid\":\"404\",\"operator\":true,\"lastSeen\":1691256802046,\"death_count\":4573,\"name\":\"404\",\"geo_info\":[{\"geolocation\":\"China\",\"date\":1659238359721}],\"banned\":false}")
        )
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
                says {
                    +"404 玩家状态 404 -m"
                }
            }
            mockGroup.addMember(simpleMemberInfo(2050, "Test2", permission = MemberPermission.MEMBER)).apply {
                says {
                    +"404 玩家状态"
                }
                says {
                    +"404 玩家状态 404 -m"
                }
            }
        }.runIFsApp(ISU())
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
                        身份: 妖怪
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
                        身份: 妖怪
                    """.trimIndent(),
                    msg.getOrNull(2)?.message?.contentToString()
                )
                fun Long.toLocalDateTime() = Instant.ofEpochMilli(this).run {
                    atZone(ZoneId.systemDefault()).toLocalDateTime()
                }
                assertEquals(
                    """
                        名字: 404    不在线
                        最后在线时间: ${1691256802046.toLocalDateTime()}
                        注册时间: ${1612772032858.toLocalDateTime()}
                        退出计数: 26    死亡计数: 4573
                        击杀玩家: 178    击杀怪物: 87419
                        OP: true    Baned: false
                        服务器: Test
                        UUID: 404
                        身份: 毛玉
                    """.trimIndent(),
                    msg.getOrNull(3)?.message?.contentToString()
                )
                assertEquals(
                    "敲，有人在假冒Test2",
                    msg.getOrNull(4)?.message?.contentToString()
                )
                assertEquals(
                    "需要权限至少为妖怪",
                    msg.getOrNull(5)?.message?.contentToString()
                )

            }
    }
}