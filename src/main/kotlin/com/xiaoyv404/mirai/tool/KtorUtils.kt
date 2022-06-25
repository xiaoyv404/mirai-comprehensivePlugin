package com.xiaoyv404.mirai.tool

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*

object KtorUtils {
    // 未使用代理的Ktor客户端
    val normalClient = HttpClient(OkHttp)

    // 安全的关闭客户端, 防止堵塞主线程
    fun closeClient() {
        normalClient.close()
    }
}