package com.xiaoyv404.mirai.app.webAPI.router

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IndexAPITest {
    @Test
    fun testIndexAPI() = testApplication {
        val response = client.get("/lab")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("欢迎来到 404Lab", response.bodyAsText())
    }
}