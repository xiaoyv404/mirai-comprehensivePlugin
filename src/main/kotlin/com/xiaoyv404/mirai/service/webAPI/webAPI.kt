package com.xiaoyv404.mirai.service.webAPI

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm.HMAC256
import com.fasterxml.jackson.databind.SerializationFeature
import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Database
import com.xiaoyv404.mirai.databace.dao.WebApiUsers
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
import org.apache.http.auth.InvalidCredentialsException
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt

open class SimpleJWT(secret: String) {
    private val algorithm = HMAC256(secret)
    val verifier = JWT.require(algorithm).build()!!
    fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
}

object WebApi {
    fun entrance() {
        Thread {
            embeddedServer(Netty, port = 8888) {
                install(ContentNegotiation) {
                    jackson {
                        enable(SerializationFeature.INDENT_OUTPUT) // 美化输出 JSON
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
                        PluginMain.logger.info("收到${post.name}登录请求")
                        val password = BCrypt.hashpw(post.password,BCrypt.gensalt(30))
                        val user = User.getOrCreat(post.name) { User.Data(name = post.name, password = password) }
                        if (user.password != password) {
                            PluginMain.logger.info("驳回${post.name}登录请求")
                            throw InvalidCredentialsException("Invalid credentials")
                        }
                        PluginMain.logger.info("${post.name}登录成功")
                        call.respond(mapOf("token" to simpleJwt.sign(user.name!!)))
                    }
                }
                routing {
                    index()
                    authenticate {
                        route("lab") {
                            lab()
                        }
                    }
                }

            }.start(wait = true)
        }.start()
    }


    private fun Route.index() {
        get("/") {
            call.respondText("Hello World!", ContentType.Text.Plain)
        }

    }


    private fun Route.lab() {
        post {
            call.respond(mapOf("OK" to true))
        }
    }


    object User{
        data class Data(
            val id: Long? = null,
            val name: String? = null,
            val password: String? = null,
            val qid: Long? = null,
            val authority: Int? = null,
        )
        fun get(name: String): Data {
            return try {
                Database.db
                    .from(WebApiUsers)
                    .select()
                    .where { WebApiUsers.name eq name }
                    .map { row ->
                        Data(
                            row[WebApiUsers.id],
                            row[WebApiUsers.name],
                            row[WebApiUsers.password],
                            row[WebApiUsers.qid],
                            row[WebApiUsers.authority]
                        )
                    }.first()
            } catch (e: IndexOutOfBoundsException) {
                Data()
            }
        }

        private fun creat(user: Data) {
            Database.db
                .insert(WebApiUsers){
                    set(it.name, user.name!!)
                    set(it.password, user.password!!)
                }
        }
        fun getOrCreat(name: String, defaultValue: () -> Data): Data {
            val user = get(name)
            return if (user.id == null){
                val answer = defaultValue()
                creat(answer)
                get(name)
            }else
                user
        }
    }
    class LoginRegister(val name: String, val password: String)

}