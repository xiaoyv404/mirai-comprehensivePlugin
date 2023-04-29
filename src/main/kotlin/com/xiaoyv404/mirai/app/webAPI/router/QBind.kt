package com.xiaoyv404.mirai.app.webAPI.router

import com.xiaoyv404.mirai.app.webAPI.*
import com.xiaoyv404.mirai.app.webAPI.controller.*
import com.xiaoyv404.mirai.dao.*
import com.xiaoyv404.mirai.extension.*
import com.xiaoyv404.mirai.model.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import kotlin.coroutines.*
import kotlin.time.Duration.Companion.minutes

fun Route.qBind() {
    post("/QBind") {
        val post = call.receive<WebApi.QQBind>()
        val principal = call.principal<UserIdPrincipal>() ?: error(WebApi.noPrincipal)
        val target = post.qqNumber.getFriend()
        if (target == null) {
            call.respond(NfResult.failed("查无此人"))
            return@post
        } else {
            call.respond(NfResult.success(null))
        }
        target.sendMessage(
            """
                                    似乎有人想绑定你的qq捏
                                    ${principal.name}是你吗w
                                    如果是你的话请输入[Y]来确认哦
                                """.trimIndent()
        )

        val qqRequest =
            withContext(EmptyCoroutineContext) {
                withTimeoutOrNull(1.minutes) {
                    GlobalEventChannel.nextEvent<FriendMessageEvent>(EventPriority.MONITOR)
                    { it.friend.id == post.qqNumber }
                }
            }?.message?.contentToString()
        if (qqRequest == "Y") {
            val targetD = WebApiUser {
                name = principal.name
            }.findByName()
            targetD!!.qid = post.qqNumber
            targetD.update()
            target.sendMessage("绑定成功~")
        } else
            target.sendMessage("不绑就不绑呗，哼")
    }
}