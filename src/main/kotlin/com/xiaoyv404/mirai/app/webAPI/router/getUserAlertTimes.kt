package com.xiaoyv404.mirai.app.webAPI.router

import com.xiaoyv404.mirai.app.webAPI.WebApi
import com.xiaoyv404.mirai.app.webAPI.controller.NfResult
import com.xiaoyv404.mirai.dao.findByQQId
import com.xiaoyv404.mirai.dao.getByWarningTimes
import com.xiaoyv404.mirai.dao.permissionRequiredAdmin
import com.xiaoyv404.mirai.model.Users
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayerQQMapping
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Route.getUserAlertTimes() {
    get("/getUserAlertTimes") {
        val principal = call.principal<UserIdPrincipal>() ?: error(WebApi.noPrincipal)
        principal.name.permissionRequiredAdmin()
        Users.getByWarningTimes(3).map {
            it.id to UserAlertTimes(times = it.warningTimes, name = MinecraftServerPlayerQQMapping {
                this.qq = it.id
            }.findByQQId()?.playerName ?: "null")
        }.let {
            call.respond(
                NfResult.success(it)
            )
        }
    }
}

@Serializable
data class UserAlertTimes(
    val times: Int,
    val name: String
)