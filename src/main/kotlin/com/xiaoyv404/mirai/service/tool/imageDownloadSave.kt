package com.xiaoyv404.mirai.service.tool

import io.ktor.client.*
import io.ktor.client.request.*
import java.io.InputStream

suspend fun downloadImage(url: String): InputStream? {
    return try {
        HttpClient().use { client -> client.get(url) }
    } catch (e: Exception) {
        println("`(*>�n<*)�������������������\n$e")
        null
    }
}