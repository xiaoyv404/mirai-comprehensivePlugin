package com.xiaoyv404.mirai.app.webAPI.router

import com.xiaoyv404.mirai.app.webAPI.*
import com.xiaoyv404.mirai.databace.dao.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.*
import net.mamoe.mirai.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import kotlin.coroutines.*
import kotlin.time.Duration.Companion.minutes

fun Route.qBind(){
    post("/QBind") {
        val post = call.receive<WebApi.QQBind>()
        val principal = call.principal<UserIdPrincipal>() ?: error(WebApi.noPrincipal)
        val bot = Bot.getInstance(2079373402)
        val target = bot.getFriend(post.qqNumber)
        if (target == null) {
            call.respond(mapOf("code" to 1000, "msg" to "查无此人"))
            return@post
        } else {
            call.respond(mapOf("code" to 200))
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