package com.xiaoyv404.mirai.app.someThing

import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.core.gid
import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.databace.dao.authorityIdentification
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.remarkOrNameCardOrNick
import net.mamoe.mirai.contact.remarkOrNick
import net.mamoe.mirai.event.events.MessageEvent

@App
class DebuMe :NfApp(),IFshApp {
    override fun getAppName() = "DebuMe"
    override fun getAppDescription() = "我是菜鸟！"
    override fun getVersion() = "1.0.0"
    override fun getCommands()= arrayOf("~me")
    override fun getLimitCount() = 2
    override fun getLimitExpiresTime() = 60L
    override fun getLimitHint() = false

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        debuMe(args.getOrNull(1), msg)
        return true
    }
    private suspend fun debuMe(data: String?, msg: MessageEvent) {
        val sender = msg.sender

        if (authorityIdentification(
                msg.uid(),
                msg.gid(),
                "DebuMe"
            )
        ) {
            val name = data ?: if (sender is Member) {
                sender.remarkOrNameCardOrNick
            } else
                sender.remarkOrNick
            msg.reply(
                "*${name}坐在地上哭着说道「可怜哒${name}什么时候才有大佬们百分之一厉害呀……」"
            )
        }
    }
}