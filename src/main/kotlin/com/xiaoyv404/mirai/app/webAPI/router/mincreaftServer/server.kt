package com.xiaoyv404.mirai.app.webAPI.router.mincreaftServer

import com.xiaoyv404.mirai.app.webAPI.*
import com.xiaoyv404.mirai.app.webAPI.controller.*
import com.xiaoyv404.mirai.dao.*
import com.xiaoyv404.mirai.entity.mincraftServer.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.server() {
    get("/server/{n}") {
        val n = call.parameters["n"] ?: error(WebApi.requestError)
        if (n == "all") {
            val map = getAll()
            call.respond(NfResult.success(map))
            return@get
        }
        val serverID = n.toIntOrNull() ?: error(WebApi.requestError)
        val data = MinecraftServer {
            this.id = serverID
        }.findById()
        call.respond(NfResult.success(data))
    }
    patch("/server/{n}") {
        val principal = call.principal<UserIdPrincipal>() ?: error(WebApi.noPrincipal)
        val n = call.parameters["n"]?.toIntOrNull() ?: error(WebApi.requestError)
        principal.name.permissionRequiredAdmin()

        val data = call.receive<MinecraftServer>()
        data.id = n
        val changeLine = data.update()
        if (changeLine == 0)
            WebApi.requestError
        else
            call.respond(NfResult.success(changeLine))
    }
}