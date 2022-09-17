package com.xiaoyv404.mirai.app.webAPI.router.admin.conversation.group.member.permission

import com.xiaoyv404.mirai.app.webAPI.*
import com.xiaoyv404.mirai.app.webAPI.controller.*
import com.xiaoyv404.mirai.databace.dao.*
import com.xiaoyv404.mirai.extension.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getGroupMemberPermission() {
    get("/getGroupMemberPermission/{gid}/{uid}") {
        val gid = call.parameters["gid"] ?: error(WebApi.requestError)
        val uid = call.parameters["uid"] ?: error(WebApi.requestError)
        val principal = call.principal<UserIdPrincipal>() ?: error(WebApi.noPrincipal)
        principal.name.permissionRequiredAdmin()
        val member = uid.getMember(gid) ?: error(WebApi.requestError)
        val permission = member.permission

        call.respond(
            NfResult.success(
                mapOf(
                    "permission" to permission
                )
            )
        )
    }
}