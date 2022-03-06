package com.xiaoyv404.mirai.tool

import com.xiaoyv404.mirai.PluginConfig
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

object KtorUtils {
    // ʹ�ô����ktor�ͻ���
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