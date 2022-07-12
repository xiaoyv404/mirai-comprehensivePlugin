package com.xiaoyv404.mirai.app.webAPI.router.admin

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.app.webAPI.*
import com.xiaoyv404.mirai.databace.dao.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getVerificationMessage() {
    get("/getVerificationMessage") {
        val principal = call.principal<UserIdPrincipal>() ?: error(WebApi.noPrincipal)
        principal.name.permissionRequiredAdmin()
        call.respond(
            mapOf(
                "code" to 200,
                "data" to NfPluginData.eventMap
            )
        )
    }
}