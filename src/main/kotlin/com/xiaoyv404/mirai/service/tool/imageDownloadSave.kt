package com.xiaoyv404.mirai.service.tool

import io.ktor.client.*
import io.ktor.client.request.*
import java.io.InputStream

suspend fun downloadImage(url: String): InputStream? {
    return try {
        HttpClient().use { client -> client.get(url) }
    } catch (e: Exception) {
        println("`(*>﹏<*)′服务器酱好像不理我惹\n$e")
        null
    }
}