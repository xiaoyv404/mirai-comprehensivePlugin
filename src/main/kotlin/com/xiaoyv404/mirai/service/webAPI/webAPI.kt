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
import io.ktor.sessions.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.remarkOrNick
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.nextEventAsync
import net.mamoe.mirai.message.code.MiraiCode
import org.apache.http.auth.InvalidCredentialsException
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.set


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
                install(WebSockets) {

                }
                install(Sessions) {
                    cookie<UserSession>(SESSION_REGISTER_NAME) {
                        cookie.path = "*" //������
                    }
                }
                routing {
                    val clients = Collections.synchronizedSet(LinkedHashSet<ChatClient>())
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
                        get {
                            call.respond("��ӭ���� 404Lab")
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
                                            // ������������
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
                                } else
                                    target.sendMessage("����Ͳ����£���")
                            }
                            route("/admin") {
                                get {
                                    val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                                    val user = User.get(principal.name)
                                    if (user.authority != 1){
                                        error("No permission")
                                    }
                                    val ses = session
                                    if (ses == null) {
                                        setSession(UserSession(
                                            user.id!!,
                                            user.authority,
                                            user.name!!,
                                            user.qid?: 0
                                        ))
                                    }
                                    call.respond("�����ǹ������Ŷ")
                                }
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
                                                "msg" to "δ���ͳɹ�",
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
            return if (user.id == null) {
                val answer = defaultValue()
                creat(answer)
                get(name)
            } else
                user
        }
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