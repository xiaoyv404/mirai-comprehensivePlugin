package com.xiaoyv404.mirai.tool

import com.xiaoyv404.mirai.PluginConfig
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

object KtorUtils {
    // 使用代理的ktor客户端
    val proxyClient = HttpClient(OkHttp) {
        engine {
            proxy = ProxyBuilder.socks("127.0.0.1", PluginConfig.database.ProxyPort)
        }
    }

    // 未使用代理的Ktor客户端
    val normalClient = HttpClient(OkHttp)

    // 安全的关闭客户端, 防止堵塞主线程
    fun closeClient() {
        proxyClient.close()
        normalClient.close()
    }
}