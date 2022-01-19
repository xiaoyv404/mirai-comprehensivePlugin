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
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.nextEventAsync
import net.mamoe.mirai.utils.MiraiExperimentalApi
import org.apache.http.auth.InvalidCredentialsException
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt

open class SimpleJWT(secret: String) {
    private val algorithm = HMAC256(secret)
    val verifier = JWT.require(algorithm).build()!!
    fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
}

@MiraiExperimentalApi
object WebApi {
    fun entrance() {
        Thread {
            embeddedServer(Netty, port = 8888) {
                install(ContentNegotiation) {
                    jackson {
                        enable(SerializationFeature.INDENT_OUTPUT) // ������� JSON
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
                    route("/lab") {
                        post("/login-register") {
                                val post = call.receive<LoginRegister>()
                                PluginMain.logger.info("�յ�${post.name}��¼����")
                                val user =
                                    User.getOrCreat(post.name) { User.Data(name = post.name, password = post.password) }
                                if (!BCrypt.checkpw(post.password, user.password)) {
                                    PluginMain.logger.info("����${post.name}��¼����")
                                    throw InvalidCredentialsException("Invalid credentials")
                                }
                                PluginMain.logger.info("${post.name}��¼�ɹ�")
                                call.respond(mapOf("token" to simpleJwt.sign(user.name!!)))
                        }
                        authenticate {
                            post("/QBind") {
                                val post = call.receive<QQBind>()
                                val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                                val bot = Bot.getInstance(2079373402)
                                val target = bot.getFriend(post.qqNumber)
                                if (target == null) {
                                    call.respond(mapOf("code" to 1000, "msg" to "���޴���"))
                                    return@post
                                } else {
                                    call.respond(mapOf("code" to 200))
                                }
                                target.sendMessage(
                                    """
                                    �ƺ�����������qq��
                                    ${principal.name}������w
                                    �������Ļ�������[Y]��ȷ��Ŷ
                                """.trimIndent()
                                )
                                val qqRequest =
                                    target.nextEventAsync<FriendMessageEvent> { it.friend.id == post.qqNumber }
                                        .await().message.contentToString()
                                if (qqRequest == "Y") {
                                    User.bindQQ(principal.name, post.qqNumber)
                                    target.sendMessage("�󶨳ɹ�~")
                                }else
                                    target.sendMessage("����Ͳ����£���")
                            }
                        }

                    }
                }
            }.start(wait = true)
        }.start()
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
              return Database.db
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
                    }.firstOrNull()?:Data()
        }


        fun bindQQ(name: String, qqNumber: Long) {
            Database.db
                .update(WebApiUsers) {
                    set(it.qid, qqNumber)
                    where { it.name eq name }
                }
        }

        /**
         * @param [user]��[Data.password]Ϊ����
         */
        private fun creat(user: Data) {
            val password = BCrypt.hashpw(user.password, BCrypt.gensalt())
            Database.db
                .insert(WebApiUsers) {
                    set(it.name, user.name!!)
                    set(it.password, password)
                }
        }

        /**
         * ��ȡ�򴴽��û���Ϣ
         */
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
    class QQBind(val qqNumber: Long)

}