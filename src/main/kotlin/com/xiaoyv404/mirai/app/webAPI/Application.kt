package com.xiaoyv404.mirai.app.webAPI

import com.xiaoyv404.mirai.app.webAPI.router.*
import com.xiaoyv404.mirai.app.webAPI.router.admin.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import org.apache.http.auth.*

fun Application.module() {
//                install(CORS) {
//                    method(HttpMethod.Options)
//                    method(HttpMethod.Get)
//                    method(HttpMethod.Post)
//                    method(HttpMethod.Put)
//                    method(HttpMethod.Delete)
//                    method(HttpMethod.Patch)
//                    header(HttpHeaders.Authorization)
//                    allowCredentials = true
//                    hosts.add("0x00.xy404.iwangtca.hello.world.chs.pub")
//                    hosts.add("")
//                }

    val simpleJwt = WebApi.SimpleJWT("my-super-secret-for-jwt")
    install(ContentNegotiation){
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
                mapOf("OK" to false, "error" to (exception.message ?: ""))
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
        route("/lab") {
            index()
            login()
            qBind()
            authenticate {
                route("/admin") {
                    adminIndex()
                    getConversationsInfoList()
                    sendMsg()
                }
            }
        }
    }
}