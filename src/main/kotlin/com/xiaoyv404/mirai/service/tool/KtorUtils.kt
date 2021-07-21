package com.xiaoyv404.mirai.service.tool

import com.xiaoyv404.mirai.PluginConfig
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.util.*

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
object KtorUtils {
    // ʹ�ô����ktor�ͻ���
    @OptIn(KtorExperimentalAPI::class)
    val proxyClient = HttpClient(OkHttp) {
        engine {
            proxy = ProxyBuilder.socks("127.0.0.1", PluginConfig.database.ProxyPort)
        }
    }

    // δʹ�ô����Ktor�ͻ���
    val normalClient = HttpClient(OkHttp)

    // ��ȫ�Ĺرտͻ���, ��ֹ�������߳�
    fun closeClient() {
        proxyClient.close()
        normalClient.close()
    }

}