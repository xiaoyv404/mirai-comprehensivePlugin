package com.xiaoyv404.mirai.app.webAPI.router.admin.thesaurus

import com.xiaoyv404.mirai.app.webAPI.*
import com.xiaoyv404.mirai.app.webAPI.controller.*
import com.xiaoyv404.mirai.databace.dao.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.finByQuestion(){
    get("/findByQuestion/{question}") {
        val q = call.parameters["question"]?: error(WebApi.requestError)
        val principal = call.principal<UserIdPrincipal>() ?: error(WebApi.noPrincipal)
        principal.name.permissionRequiredAdmin()
        val reply = Thesauru{
            question = q
        }.findByQuestion("")
        call.respond(
            NfResult.success(reply)
        )
    }
}