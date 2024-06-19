package com.xiaoyv404.mirai.app.webAPI.router.mincreaftServer

import com.xiaoyv404.mirai.app.webAPI.WebApi
import com.xiaoyv404.mirai.app.webAPI.controller.NfResult
import com.xiaoyv404.mirai.app.webAPI.model.apiModel
import com.xiaoyv404.mirai.dao.findById
import com.xiaoyv404.mirai.dao.getAll
import com.xiaoyv404.mirai.dao.permissionRequiredAdmin
import com.xiaoyv404.mirai.dao.update
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServer
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.server() {
    get("/server/{id}") {
        val id = call.parameters["id"] ?: error(WebApi.requestError)
        if (id == "all") {
            val map = getAll().map { it.apiModel() }
            call.respond(NfResult.success(map))
            return@get
        }
        val serverID = id.toIntOrNull() ?: error(WebApi.requestError)

        val data = MinecraftServer {
            this.id = serverID
        }.findById()?.apiModel()

        call.respond(NfResult.success(data))
    }
    patch("/server/{id}") {
        val principal = call.principal<UserIdPrincipal>() ?: error(WebApi.noPrincipal)
        val id = call.parameters["id"]?.toIntOrNull() ?: error(WebApi.requestError)
        principal.name.permissionRequiredAdmin()

        val data = call.receive<MinecraftServer>()
        data.id = id
        val changeLine = data.update()
        if (changeLine == 0)
            WebApi.requestError
        else
            call.respond(NfResult.success(changeLine))
    }
}