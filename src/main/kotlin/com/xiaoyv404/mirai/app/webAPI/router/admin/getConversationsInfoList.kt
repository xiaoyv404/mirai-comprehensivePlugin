package com.xiaoyv404.mirai.app.webAPI.router.admin

import com.xiaoyv404.mirai.app.webAPI.*
import com.xiaoyv404.mirai.databace.dao.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*
import net.mamoe.mirai.*
import net.mamoe.mirai.contact.*

fun Route.getConversationsInfoList(){
    post("/getConversationsInfoList") {
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
            mapOf(
                "code" to 200,
                "data" to mapOf("groups" to groups, "friends" to friends)
            )
        )
    }
}