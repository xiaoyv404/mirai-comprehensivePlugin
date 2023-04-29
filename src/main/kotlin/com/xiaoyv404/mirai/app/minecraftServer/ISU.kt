package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.dao.*
import com.xiaoyv404.mirai.model.mincraftServer.*
import net.mamoe.mirai.event.events.*
import java.time.*

@App
class ISU : NfApp(), IFshApp {
    override fun getAppName() = "ISU"

    override fun getVersion() = "1.0.1"

    override fun getAppDescription() = "我的世界玩家状态监控"

    override fun getCommands() = arrayOf("-玩家状态", "-有妖怪在线吗", "-桃呢", "-有无妖怪")

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        return when (args[0]) {
            "-玩家状态", "-桃呢" -> isOnline(args, msg)
            "-有妖怪在线吗", "-有无妖怪" -> findOP(msg)
            else -> false
        }
    }

    private suspend fun isOnline(args: Array<String>, msg: MessageEvent): Boolean {
        if (args.size < 2 && args[0] != "-桃呢")
            return false
        val name = if (args[0] == "-桃呢") "2429334909" else args[1]
        val player = if (msg.gid() == 113594190L)
            MinecraftServerPlayer {
                this.name = name
                this.lastLoginServer = "gtnh"
            }.findByNameAndServer()
        else
            MinecraftServerPlayer {
                this.name = name
            }.findByName()

        if (player == null) {
            msg.reply("无数据")
            return false
        }

        msg.reply(
            """
                名字: ${player.name}
                ${if (Duration.between(player.lastLoginTime, LocalDateTime.now()).toMinutes() > 4) "不在线" else "在线"}
                最后在线时间: ${player.lastLoginTime}
                服务器: ${player.lastLoginServer}
                UUID: ${player.id}
                身份: ${player.permissions?.getPermissionByCode()?.permissionName ?: "毛玉"}
            """.trimIndent()
        )
        return true
    }

    private suspend fun findOP(msg: MessageEvent): Boolean {
        val op = MinecraftServerPlayer().getAllOnlinePlayers().toMutableList()
        op.removeIf {
            it.permissions == null
        }
        if (op.isEmpty())
            msg.reply("没有呢 :(")
        else
            msg.reply("有: ${op.joinToString(", ") { it.name }}")

        return true
    }
}