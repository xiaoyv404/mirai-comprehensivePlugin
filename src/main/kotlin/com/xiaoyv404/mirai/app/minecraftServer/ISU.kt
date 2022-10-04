package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.databace.dao.*
import net.mamoe.mirai.event.events.*
import java.time.*

@App
class ISU : NfApp(), IFshApp {
    override fun getAppName() = "ISU"

    override fun getVersion() = "1.0.0"

    override fun getAppDescription() = "我的世界玩家状态监控"

    override fun getCommands() = arrayOf("-桃子在线不")

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        val player = MinecraftServerPlayer {
            this.name = "2429334909"
        }.findByName()

        if (player == null) {
            msg.reply("屑桃子今天就没上线过")
        } else
            msg.reply(
                """
                ${if (Duration.between(player.lastLoginTime, LocalDateTime.now()).toMinutes() > 4) "不在线" else "在线"}
                最后在线时间: ${player.lastLoginTime}
            """.trimIndent()
            )

        return true
    }
}