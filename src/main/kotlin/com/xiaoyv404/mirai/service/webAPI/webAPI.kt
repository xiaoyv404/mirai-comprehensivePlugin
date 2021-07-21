package com.xiaoyv404.mirai.service.webAPI

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun webAPIEntrance() {
    Thread {
        embeddedServer(Netty, port = 8080) {
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
        call.respondText("Hello World!?", ContentType.Text.Plain)
    }
    get("/test") {
        call.respondText("test", ContentType.Text.Plain)
    }
}