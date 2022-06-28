package com.xiaoyv404.mirai.tool

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import java.io.*

open class NfHttpClient : Closeable {
    override fun close() = clients.forEach { it.close() }

    private val clients = MutableList(3) { client() }
    private fun client() = HttpClient(OkHttp)

    private var clientIndex = 0
    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        while (isActive) {
            try {
                val client = clients[clientIndex]
                return@supervisorScope block(client)
            } catch (throwable: Throwable) {
                if (isActive && (throwable is IOException || throwable is HttpRequestTimeoutException)) {
                    clientIndex = (clientIndex + 1) % clients.size
                } else {
                    throw throwable
                }
            }
        }
        throw CancellationException()
    }

    suspend inline fun <reified T> get(url: String, crossinline block: HttpRequestBuilder.() -> Unit = {}) =
        useHttpClient<T> {
            it.get(url) {
                block()
            }.body()
        }

}

object ClientUtils {
    lateinit var normalClient: NfHttpClient

    fun init() {
        normalClient = NfHttpClient()
        println("Http客户端运行正常")
    }
}