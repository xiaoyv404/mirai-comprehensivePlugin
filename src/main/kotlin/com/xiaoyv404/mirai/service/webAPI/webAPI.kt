package com.xiaoyv404.mirai.service.webAPI

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.algorithms.Algorithm.HMAC256
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import org.apache.http.auth.InvalidCredentialsException
import java.util.*

data class SampleSession(val name: String, val value: Int)


fun webAPIEntrance() {
    Thread {
        embeddedServer(Netty, port = 8888) {
            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT) // ÃÀ»¯Êä³ö JSON
                }
            }
            val simpleJwt = SimpleJWT("my-super-secret-for-jwt")
            install(Authentication) {
                jwt {
                    verifier(simpleJwt.verifier)
                    validate {
                        UserIdPrincipal(it.payload.getClaim("name").asString())
                    }
                }
            }
            install(StatusPages) {
                exception<InvalidCredentialsException> { exception ->
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("OK" to false, "error" to (exception.message ?: ""))
                    )
                }
            }
            routing {
                post("/login-register") {
                    val post = call.receive<LoginRegister>()
                    val user = users.getOrPut(post.user) { User(post.user, post.password) }
                    if (user.password != post.password) throw InvalidCredentialsException("Invalid credentials")
                    call.respond(mapOf("token" to simpleJwt.sign(user.name)))
                }
            }
            routing {
                index()
                authenticate {
                    route("test") {
                        test()
                    }
                }
            }

        }.start(wait = true)
    }.start()
}

open class SimpleJWT(val secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
}

class User(val name: String, val password: String)

val users = Collections.synchronizedMap(
    listOf(User("test", "test"))
        .associateBy { it.name }
        .toMutableMap()
)

class LoginRegister(val user: String, val password: String)


fun Route.index() {
    get("/") {
        call.respondText("Hello World!", ContentType.Text.Plain)
    }

}



fun Route.test() {
    post {
        call.respond(mapOf("OK" to true))
    }
    get("/test") {
        call.respondText("test", ContentType.Text.Plain)
    }
}