package com.xiaoyv404.mirai.app.webAPI

import com.xiaoyv404.mirai.app.webAPI.router.*
import com.xiaoyv404.mirai.app.webAPI.router.admin.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
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
import org.apache.http.auth.*

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
        json()
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
        exception<InvalidCredentialsException> { call, exception ->
            call.respond(
                HttpStatusCode.Unauthorized,
                mapOf("code" to "401", "error" to (exception.message ?: ""))
            )
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
        post("/v1/android/sign") {
            call.respond(
                mapOf(
                    "code" to "200",
                    "message" to "登录成功"
                )
            )
        }
        route("/lab") {
            index()
            login()
            qBind()
            authenticate {
                route("/admin") {
                    adminIndex()
                    getConversationsInfoList()
                    sendMsg()
                    getVerificationMessage()
                }
            }
        }
    }
}