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
import io.ktor.http.cio.websocket.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.remarkOrNick
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.nextEventAsync
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.utils.MiraiExperimentalApi
import org.apache.http.auth.InvalidCredentialsException
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

open class SimpleJWT(secret: String) {
    private val algorithm = HMAC256(secret)
    val verifier = JWT.require(algorithm).build()!!
    fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
}


@MiraiExperimentalApi
object WebApi {
    class ChatClient(val session: DefaultWebSocketSession) {
        companion object { var lastId = AtomicInteger(0) }
        val id = lastId.getAndIncrement()
        val name = "user$id"
    }
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
                install(WebSockets){

                }
                routing {
                    val clients = Collections.synchronizedSet(LinkedHashSet<ChatClient>())
                    route("/lab") {
                        post("/login-register") {
                                val post = call.receive<LoginRegister>()
                                PluginMain.logger.info("收到${post.name}登录请求")
                                val user =
                                    User.getOrCreat(post.name) { User.Data(name = post.name, password = post.password) }
                                if (!BCrypt.checkpw(post.password, user.password)) {
                                    PluginMain.logger.info("驳回${post.name}登录请求")
                                    throw InvalidCredentialsException("Invalid credentials")
                                }
                                PluginMain.logger.info("${post.name}登录成功")
                                call.respond(mapOf("token" to simpleJwt.sign(user.name!!)))
                        }
                        get {
                            call.respond("欢迎来到 404Lab")
                        }
                        webSocket("/admin/listenMsg") {
                            val client = ChatClient(this)
                            clients += client
                            try {
                                while (true) {
                                    when (val frame = incoming.receive()) {
                                        is Frame.Text -> {
                                            val text = frame.readText()
                                            // 迭代所有连接
                                            val textToSend = "${client.name} said: $text"
                                            for (other in clients.toList()) {
                                                other.session.outgoing.send(Frame.Text(textToSend))
                                            }
                                        }
                                        else          -> {}
                                    }
                                }
                            } finally {
                                clients -= client
                            }
                        }
                        authenticate {
                            post("/QBind") {
                                val post = call.receive<QQBind>()
                                val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
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
                                    target.nextEventAsync<FriendMessageEvent> { it.friend.id == post.qqNumber }
                                        .await().message.contentToString()
                                if (qqRequest == "Y") {
                                    User.bindQQ(principal.name, post.qqNumber)
                                    target.sendMessage("绑定成功~")
                                } else
                                    target.sendMessage("不绑就不绑呗，哼")
                            }
                            route("/admin") {
                                post("/getConversationsInfoList") {
                                    val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                                    if (User.get(principal.name).authority != 1) {
                                        error("No permission")
                                    }
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
                                post("/sendMsg") {
                                    val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                                    val post = call.receive<SendMsg>()
                                    if (User.get(principal.name).authority != 1) {
                                        error("No permission")
                                    }
                                    val bot = Bot.getInstance(2079373402)
                                    val fail = mutableListOf<Long>()
                                    post.targets.forEach {
                                        val target = bot.getGroup(it)
                                        if (target == null) {
                                            fail.add(it)
                                        } else
                                            target.sendMessage(MiraiCode.deserializeMiraiCode(post.msg))
                                    }
                                    if (fail.isNotEmpty())
                                        call.respond(
                                            mapOf(
                                                "code" to 10001,
                                                "msg" to "未发送成功",
                                                "list" to fail
                                            )
                                        )
                                    else
                                        call.respond(mapOf("code" to 200))
                                }
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
         * @param [user]内[Data.password]为明文
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
         * 获取或创建用户信息
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

    class SendMsg(val targets: List<Long>, val msg: String)
    class LoginRegister(val name: String, val password: String)
    class QQBind(val qqNumber: Long)

}