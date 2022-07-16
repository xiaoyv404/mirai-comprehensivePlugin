package com.xiaoyv404.mirai.app.webAPI.router.admin.action

import com.xiaoyv404.mirai.app.webAPI.*
import com.xiaoyv404.mirai.databace.dao.*
import com.xiaoyv404.mirai.extension.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.mamoe.mirai.message.code.*

fun Route.sendMsg() {
    post("/sendMsg") {
        val principal = call.principal<UserIdPrincipal>() ?: error(WebApi.noPrincipal)
        val post = call.receive<WebApi.SendMsg>()
        principal.name.permissionRequiredAdmin()

        val fail = mutableListOf<Long>()
        post.targets.forEach {
            val target = it.getGroup()
            if (target == null) {
                fail.add(it)
            } else
                target.sendMessage(MiraiCode.deserializeMiraiCode(post.msg))
        }
        if (fail.isNotEmpty())
            call.respond(
                mapOf(
                    "code" to 10001,
                    "msg" to "未发送成功",
                    "list" to fail
                )
            )
        else
            call.respond(mapOf("code" to 200))
    }
}