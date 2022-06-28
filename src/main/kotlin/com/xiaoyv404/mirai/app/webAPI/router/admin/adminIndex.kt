package com.xiaoyv404.mirai.app.webAPI.router.admin

import com.xiaoyv404.mirai.app.webAPI.*
import com.xiaoyv404.mirai.databace.dao.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.adminIndex() {
    get {
        val principal = call.principal<UserIdPrincipal>() ?: error(WebApi.noPrincipal)
        principal.name.permissionRequiredAdmin()
        call.respond("这里是管理界面哦")
    }
}