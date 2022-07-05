package com.xiaoyv404.mirai.app.webAPI.router

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.app.webAPI.*
import com.xiaoyv404.mirai.databace.dao.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.*

fun Route.login() {
    post("/login") {
        val post = call.receive<WebApi.LoginRegister>()
        PluginMain.logger.info("收到${post.name}登录请求")
        val user = WebApiUser {
            name = post.name
            password = post.password
        }.findByNameOrSave()
        if (!BCrypt.checkpw(post.password, user.password)) {
            PluginMain.logger.info("驳回${post.name}登录请求")
            call.respond(mapOf("code" to "1000","msg" to "密码错误"))
        }
        PluginMain.logger.info("${post.name}登录成功")
        call.respond(mapOf("code" to "200","token" to WebApi.simpleJwt.sign(user.name)))
    }
}