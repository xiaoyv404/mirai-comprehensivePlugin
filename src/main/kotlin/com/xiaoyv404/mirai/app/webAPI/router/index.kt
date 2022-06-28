package com.xiaoyv404.mirai.app.webAPI.router

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.index() {
    get {
        call.respond("欢迎来到 404Lab")
    }
}