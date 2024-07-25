package com.xiaoyv404.mirai.app.webAPI.router.mincreaftServer

import com.xiaoyv404.mirai.app.webAPI.WebApi
import com.xiaoyv404.mirai.app.webAPI.controller.NfResult
import com.xiaoyv404.mirai.app.webAPI.model.MinecraftPlayerApiModel
import com.xiaoyv404.mirai.dao.findById
import com.xiaoyv404.mirai.dao.findByName
import com.xiaoyv404.mirai.dao.getAllOnlinePlayers
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayer
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.player() {
    get("/players/{id}") {
        val id = call.parameters["id"] ?: error(WebApi.requestError)
        val data = MinecraftServerPlayer {
            this.id = id
        }.findById() ?: error(WebApi.requestError)
        val apiModel = data.let {
            MinecraftPlayerApiModel(
                it.name,
                it.id,
                it.lastLoginTime.toString(),
                it.lastLoginServer,
                it.permissions
            )
        }
        call.respond(NfResult.success(apiModel))
    }

    get("/players/search") {
        val name = call.request.queryParameters["name"] ?: error(WebApi.requestError)
        val data = MinecraftServerPlayer {
            this.name = name
        }.findByName().map {
            MinecraftPlayerApiModel(
                it.name,
                it.id,
                it.lastLoginTime.toString(),
                it.lastLoginServer,
                it.permissions
            )
        }
        call.respond(NfResult.success(data))
    }
    get("/players/online") {
        val data = MinecraftServerPlayer{}.getAllOnlinePlayers().map {
            MinecraftPlayerApiModel(
                it.name,
                it.id,
                it.lastLoginTime.toString(),
                it.lastLoginServer,
                it.permissions
            )
        }
        call.respond(NfResult.success(data))
    }
}