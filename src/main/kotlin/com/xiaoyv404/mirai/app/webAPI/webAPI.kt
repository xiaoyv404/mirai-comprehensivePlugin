package com.xiaoyv404.mirai.app.webAPI

import com.auth0.jwt.*
import com.auth0.jwt.algorithms.Algorithm.*
import com.xiaoyv404.mirai.core.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*


@App
class WebApi : NfApp() {
    override fun getAppName() = "WebApi"
    override fun getVersion() = "1.0.1"
    override fun getAppDescription() = "网络API"

    override fun init() {
        embeddedServer(Netty, port = 4040, module = Application::module).start()
    }

    open class SimpleJWT(secret: String) {
        private val algorithm = HMAC256(secret)
        val verifier = JWT.require(algorithm).build()!!
        fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
    }

    class SendMsg(val targets: List<Long>, val msg: String)

    class LoginRegister(val name: String, val password: String)

    class QQBind(val qqNumber: Long)

    data class UserSession(
        val uid: Long,
        val authority: Int,
        val name: String,
        val qid: Long
    )

    companion object {
        const val SESSION_REGISTER_NAME = "ktor-404"
        const val noPrincipal = "No principal"
        const val requestError = "Request Error"
        val simpleJwt = SimpleJWT("my-super-secret-for-jwt")
    }
}