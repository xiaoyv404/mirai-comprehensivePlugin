package com.xiaoyv404.mirai.tool

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.coroutines.*

object ClientUtils {
    private val client = HttpClient(OkHttp)

    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        block(client)
    }

    suspend inline fun <reified T> get(url: String, crossinline block: HttpRequestBuilder.() -> Unit = {}) =
        useHttpClient<T> {
            it.get(url) {
                block()
            }.body()
        }

    fun uninit() {
        client.close()
    }
}