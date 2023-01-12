package com.xiaoyv404.mirai.app.webAPI.router.mincreaftServer

import com.xiaoyv404.mirai.app.webAPI.controller.*
import com.xiaoyv404.mirai.databace.dao.mincraftServer.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getAllServer() {
    get("/getAllServer") {
        val map = getAll()
        call.respond(NfResult.success(map))
    }
}