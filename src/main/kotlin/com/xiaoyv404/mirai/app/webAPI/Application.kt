package com.xiaoyv404.mirai.app.webAPI

import com.xiaoyv404.mirai.app.webAPI.router.admin.action.sendMsg
import com.xiaoyv404.mirai.app.webAPI.router.admin.adminIndex
import com.xiaoyv404.mirai.app.webAPI.router.admin.conversation.getConversationsInfoList
import com.xiaoyv404.mirai.app.webAPI.router.admin.conversation.group.member.getGroupMemberList
import com.xiaoyv404.mirai.app.webAPI.router.admin.conversation.group.member.permission.getGroupMemberPermission
import com.xiaoyv404.mirai.app.webAPI.router.admin.core.coreRouter
import com.xiaoyv404.mirai.app.webAPI.router.admin.event.getVerificationMessage
import com.xiaoyv404.mirai.app.webAPI.router.admin.thesaurus.thesaurusRouter
import com.xiaoyv404.mirai.app.webAPI.router.getUserAlertTimes
import com.xiaoyv404.mirai.app.webAPI.router.index
import com.xiaoyv404.mirai.app.webAPI.router.login
import com.xiaoyv404.mirai.app.webAPI.router.mincreaftServer.minecraftSeverRouter
import com.xiaoyv404.mirai.app.webAPI.router.qBind
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*

fun Application.module() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = true
        anyHost()
    }

    val simpleJwt = WebApi.SimpleJWT("my-super-secret-for-jwt")
    install(ContentNegotiation) {
        gson()
    }
    install(Authentication) {
        jwt {
            verifier(simpleJwt.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }
    }

    install(StatusPages) {
        exception<Error> { call, exception ->
            when (exception.message) {
                WebApi.noPrincipal -> call.respond(
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("code" to "401", "error" to (exception.message ?: ""))
                    )
                )
                WebApi.requestError -> call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("code" to "404", "msg" to "Not Found")
                )
            }
        }

    }
    install(WebSockets) {

    }
    install(Sessions) {
        cookie<WebApi.UserSession>(WebApi.SESSION_REGISTER_NAME) {
            cookie.path = "*" //测试用
        }
    }

    routing {
        route("/lab") {
            index()
            login()
            qBind()
            minecraftSeverRouter()
            authenticate {
                route("/admin") {
                    adminIndex()
                    getConversationsInfoList()
                    sendMsg()
                    getVerificationMessage()
                    getGroupMemberList()
                    getGroupMemberPermission()
                    getUserAlertTimes()
                    thesaurusRouter()
                    coreRouter()
                }
            }
        }
    }
}