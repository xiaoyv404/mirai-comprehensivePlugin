package com.xiaoyv404.mirai.app.webAPI.router.mincreaftServer

import com.xiaoyv404.mirai.DatabaseTest
import com.xiaoyv404.mirai.core.NfClock
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId


internal class PlayerKtTest : DatabaseTest() {
    @Test
    fun testGetLabMinecraftSeverPlayersOnline() = testApplication {
        val clock = Clock.fixed(Instant.parse("2006-04-16T06:59:39.00Z"), ZoneId.of("UTC"))
        NfClock.mockTime(clock)
        client.get("/lab/minecraftSever/players/online").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("{\"code\":2001,\"msg\":\"接口调用成功\",\"data\":[{\"id\":\"Test\",\"name\":\"test\",\"lastLoginTime\":\"2006-04-16T06:58:39.810\",\"lastLoginServer\":\"Test\",\"permissions\":\"OP\"},{\"id\":\"Test2\",\"name\":\"test2\",\"lastLoginTime\":\"2006-04-16T06:58:39.810\",\"lastLoginServer\":\"Test\",\"permissions\":\"Default\"},{\"id\":\"2429334909\",\"name\":\"2429334909\",\"lastLoginTime\":\"2006-04-16T06:58:39.810\",\"lastLoginServer\":\"Test\",\"permissions\":\"Default\"}]}", bodyAsText())
        }
    }
}