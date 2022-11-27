package com.xiaoyv404.mirai.app.webAPI.router.admin.core

import com.xiaoyv404.mirai.app.webAPI.*
import com.xiaoyv404.mirai.app.webAPI.controller.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.databace.dao.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getOnlineApps() {
    get("/getOnlineAppsName") {
        val principal = call.principal<UserIdPrincipal>() ?: error(WebApi.noPrincipal)
        principal.name.permissionRequiredAdmin()

        val data = NfApplicationManager.nfApps.map { it.getAppName() }
        call.respond(
            NfResult.success(data)
        )
    }
}