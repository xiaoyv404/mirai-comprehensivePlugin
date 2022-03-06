package com.xiaoyv404.mirai.app.webAPI

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm.HMAC256
import com.fasterxml.jackson.databind.SerializationFeature
import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.dao.*
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
import io.ktor.sessions.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.remarkOrNick
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.nextEvent
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.utils.MiraiExperimentalApi
import org.apache.http.auth.InvalidCredentialsException
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.set
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.minutes


@OptIn(MiraiExperimentalApi::class)
object WebApi {
    private inline val WebSocketServerSession.session: UserSession?
        get() = try {
            call.sessions.get(SESSION_REGISTER_NAME) as? UserSession
        } catch (th: Throwable) {
            null
        }
    private inline val PipelineContext<*, ApplicationCall>.session: UserSession?
        get() = try {
            context.sessions.get(SESSION_REGISTER_NAME) as? UserSession
        } catch (th: Throwable) {
            null
        }

    private fun PipelineContext<*, ApplicationCall>.setSession(us: UserSession) =
        context.sessions.set(SESSION_REGISTER_NAME, us)

    private const val SESSION_REGISTER_NAME = "ktor-404"

    open class SimpleJWT(secret: String) {
        private val algorithm = HMAC256(secret)
        val verifier = JWT.require(algorithm).build()!!
        fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
    }

    class ChatClient(val session: DefaultWebSocketSession) {
        companion object {
            var lastId = AtomicInteger(0)
        }

        val id = lastId.getAndIncrement()
        val name = "user$id"
    }

    fun entrance() {
        Thread {
            embeddedServer(Netty, port = 4040) {
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
                install(WebSockets) {

                }
                install(Sessions) {
                    cookie<UserSession>(SESSION_REGISTER_NAME) {
                        cookie.path = "*" //测试用
                    }
                }
                routing {
                    val clients = Collections.synchronizedSet(LinkedHashSet<ChatClient>())
                    route("/lab") {
                        post("/login-register") {
                            val post = call.receive<LoginRegister>()
                            PluginMain.logger.info("收到${post.name}登录请求")
                            val user = WebApiUser {
                                name = post.name
                                password = post.password
                            }.findByNameOrSave()
                            if (!BCrypt.checkpw(post.password, user.password)) {
                                PluginMain.logger.info("驳回${post.name}登录请求")
                                throw InvalidCredentialsException("Invalid credentials")
                            }
                            PluginMain.logger.info("${post.name}登录成功")
                            call.respond(mapOf("token" to simpleJwt.sign(user.name)))
                        }

                        get {
                            call.respond("欢迎来到 404Lab")
                        }
                        webSocket("/admin/listenMsg") {
                            val ses = session
                            if (ses == null) {
                                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                                return@webSocket
                            }

                            val get = call.receive<Chat>()
                            println(get.token)
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
                                    withContext(EmptyCoroutineContext) {
                                        withTimeoutOrNull(1.minutes) {
                                            GlobalEventChannel.nextEvent<FriendMessageEvent>(EventPriority.MONITOR)
                                            { it.friend.id == post.qqNumber }
                                        }
                                    }?.message?.contentToString()
                                if (qqRequest == "Y") {
                                    val targetD = WebApiUser {
                                        name = principal.name
                                    }.findByName()
                                    targetD!!.qid = post.qqNumber
                                    targetD.update()
                                    target.sendMessage("绑定成功~")
                                } else
                                    target.sendMessage("不绑就不绑呗，哼")
                            }
                            route("/admin") {
                                get {
                                    val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                                    val user = WebApiUser {
                                        name = principal.name
                                    }.findByName()!!
                                    if (user.authority != 1) {
                                        error("No permission")
                                    }
                                    val ses = session
                                    if (ses == null) {
                                        setSession(
                                            UserSession(
                                                user.id,
                                                user.authority,
                                                user.name,
                                                user.qid
                                            )
                                        )
                                    }
                                    call.respond("这里是管理界面哦")
                                }
                                post("/getConversationsInfoList") {
                                    val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
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
                                post("/sendMsg") {
                                    val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                                    val post = call.receive<SendMsg>()
                                    principal.name.permissionRequiredAdmin()

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

    class SendMsg(val targets: List<Long>, val msg: String)
    class LoginRegister(val name: String, val password: String)
    class Chat(val token: String)
    class QQBind(val qqNumber: Long)
    data class UserSession(
        val uid: Long,
        val authority: Int,
        val name: String,
        val qid: Long
    )
}