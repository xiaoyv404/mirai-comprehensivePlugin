package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.core.gid
import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.dao.findByName
import com.xiaoyv404.mirai.dao.findByNameAndServer
import com.xiaoyv404.mirai.dao.findByPlayerName
import com.xiaoyv404.mirai.dao.save
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayer
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayerQQMapping
import net.mamoe.mirai.event.events.MessageEvent
import java.time.Duration
import java.time.LocalDateTime

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

    val regex = Regex("\\w+")

    private suspend fun isOnline(args: Array<String>, msg: MessageEvent): Boolean {
        val name = when {
            args[0] == "-桃呢" -> "2429334909"
            args.getOrNull(1) != null -> args[1]
            else -> regex.find(msg.senderName)?.value
        }
        if (name == null) {
            msg.reply("快去修改群名片为自己的服务器ID！")
            return false
        }

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
            if (args.getOrNull(1) == null)
                msg.reply("无数据呢，请把服务器ID放到群名片的最前面")
            else
                msg.reply("无数据")
            return false
        }
        if (args.getOrNull(1) == null)
            MinecraftServerPlayerQQMapping {
                this.playerName = player.name
            }.findByPlayerName().find {
                it.lock
            }?.let {
                if (it.lock && msg.uid() != it.qq) {
                    msg.reply("敲，有人在假冒${player.name}")
                    return true
                }

                MinecraftServerPlayerQQMapping {
                    this.qq = msg.uid()
                    this.playerName = player.name
                }.save()
            }




        msg.reply(
            """
                名字: ${player.name}
                ${if (Duration.between(player.lastLoginTime, LocalDateTime.now()).toMinutes() > 4) "不在线" else "在线"}
                最后在线时间: ${player.lastLoginTime}
                服务器: ${player.lastLoginServer}
                UUID: ${player.id}
                身份: ${player.permissions.permissionName}
            """.trimIndent()
        )
        return true
    }

    private suspend fun findOP(msg: MessageEvent): Boolean {
        val op = MinecraftServerPlayer().getAllOnlinePlayers().toMutableList()
        op.removeIf {
            it.permissions.code == null
        }
        if (op.isEmpty())
            msg.reply("没有呢 :(")
        else
            msg.reply("有: ${op.joinToString(", ") { it.name }}")

        return true
    }
}