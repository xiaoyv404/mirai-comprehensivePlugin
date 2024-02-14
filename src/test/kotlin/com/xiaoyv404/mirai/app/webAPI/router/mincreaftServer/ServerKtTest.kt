package com.xiaoyv404.mirai.app.webAPI.router.mincreaftServer

import com.xiaoyv404.mirai.DatabaseTest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


internal class ServerKtTest : DatabaseTest() {
    @Test
    fun testGetLabMinecraftseverServerN() = testApplication {
        client.get("/lab/minecraftSever/server/all").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("{\"code\":2001,\"msg\":\"接口调用成功\",\"data\":[{\"name\":\"mcg\",\"host\":\"test\",\"port\":1,\"status\":1,\"playerNum\":10,\"playerMaxNum\":80}]}", bodyAsText())
        }
    }
}