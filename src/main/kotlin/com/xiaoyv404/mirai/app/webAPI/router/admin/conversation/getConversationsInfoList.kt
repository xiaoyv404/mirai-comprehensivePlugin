package com.xiaoyv404.mirai.app.webAPI.router.admin.conversation

import com.xiaoyv404.mirai.app.webAPI.*
import com.xiaoyv404.mirai.app.webAPI.controller.*
import com.xiaoyv404.mirai.databace.dao.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.mamoe.mirai.*
import net.mamoe.mirai.contact.*

fun Route.getConversationsInfoList() {
    post("/getConversationsInfoList") {
        try {


            val principal = call.principal<UserIdPrincipal>() ?: error(WebApi.noPrincipal)
            principal.name.permissionRequiredAdmin()

            val bot = Bot.getInstance(2079373402)
            val groups = mutableMapOf<Long, Map<String, Any>>()
            bot.groups.forEach {
                val group = mapOf<String, Any>(
                    "name" to it.name,
                    "botPermission" to it.botPermission,
                    "avatarUrl" to it.avatarUrl,
                )
                groups[it.id] = group
            }

            val friends = mutableMapOf<Long, Map<String, Any>>()
            bot.friends.forEach {
                val friend = mapOf<String, Any>(
                    "name" to it.remarkOrNick,
                    "avatarUrl" to it.avatarUrl,
                )
                friends[it.id] = friend
            }

            call.respond(
                NfResult.success(mapOf("groups" to groups, "friends" to friends))
            )
        }catch (e:Exception){
            println(e.stackTrace)
        }
    }
}