package com.xiaoyv404.mirai.app.webAPI.router.admin.conversation.group.member

import com.xiaoyv404.mirai.app.webAPI.*
import com.xiaoyv404.mirai.databace.dao.*
import com.xiaoyv404.mirai.extension.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getGroupMemberList() {
    get("/getGroupMemberList/{gid}") {
        val gid = call.parameters["gid"]
        val principal = call.principal<UserIdPrincipal>() ?: error(WebApi.noPrincipal)
        principal.name.permissionRequiredAdmin()
        if (gid == null)
            error(WebApi.requestError)
        val group = gid.getGroup() ?: error(WebApi.requestError)
        call.respond(
            mapOf(
                "code" to "200",
                "data" to group.members
            )
        )
    }
}