package com.xiaoyv404.mirai.app.webAPI.router

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.index(){
    get {
        call.respond("欢迎来到 404Lab")
    }
}