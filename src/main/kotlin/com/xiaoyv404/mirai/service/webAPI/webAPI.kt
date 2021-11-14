package com.xiaoyv404.mirai.service.webAPI

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*


fun webAPIEntrance() {
    Thread {
        embeddedServer(Netty, port = 8080) {
            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT) // ÃÀ»¯Êä³ö JSON
                }
            }
            routing {
                index()
                route("test") {
                    test()
                }
            }
        }.start(wait = true)
    }.start()
}

fun Route.index() {
    get("/") {
        call.respondText("Hello World!", ContentType.Text.Plain)
    }
}

fun Route.test() {
    get {
        call.respond(mapOf("OK" to true))
    }
    get("/test") {
        call.respondText("test",ContentType.Text.Plain)
    }
}