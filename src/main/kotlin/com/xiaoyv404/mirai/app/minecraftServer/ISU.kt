package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.databace.dao.mincraftServer.*
import net.mamoe.mirai.event.events.*
import java.time.*

@App
class ISU : NfApp(), IFshApp {
    override fun getAppName() = "ISU"

    override fun getVersion() = "1.0.0"

    override fun getAppDescription() = "我的世界玩家状态监控"

    override fun getCommands() = arrayOf("-玩家状态")

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        val player = if(args.size>=2){
             MinecraftServerPlayer {
                this.name = args[1]
            }.findByName()
        }else
            null

        if (player == null) {
            msg.reply("无数据")
        } else
            msg.reply(
                """
                名字: ${player.name}
                ${if (Duration.between(player.lastLoginTime, LocalDateTime.now()).toMinutes() > 4) "不在线" else "在线"}
                最后在线时间: ${player.lastLoginTime}
                服务器: ${player.lastLoginServer}
                UUID: ${player.id}
            """.trimIndent()
            )
        return true
    }
}