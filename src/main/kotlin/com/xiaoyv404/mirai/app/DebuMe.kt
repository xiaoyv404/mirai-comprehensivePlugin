package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.databace.dao.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.events.*

@App
class DebuMe : NfApp(), IFshApp {
    override fun getAppName() = "DebuMe"
    override fun getAppDescription() = "我是菜鸟！"
    override fun getVersion() = "1.0.0"
    override fun getCommands() = arrayOf("~me")
    override fun getLimitCount() = 1
    override fun getLimitExpiresTime() = 180L
    override fun getLimitHint() = false

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        return debuMe(args.getOrNull(1), msg)
    }

    private suspend fun debuMe(data: String?, msg: MessageEvent): Boolean {
        val sender = msg.sender

        if (msg.authorityIdentification("DebuMe")
        ) return false

        val name = data ?: if (sender is Member) {
            sender.remarkOrNameCardOrNick
        } else
            sender.remarkOrNick
        msg.reply(
            "*${name}坐在地上哭着说道「可怜哒${name}什么时候才有大佬们百分之一厉害呀……」"
        )
        return true
    }
}