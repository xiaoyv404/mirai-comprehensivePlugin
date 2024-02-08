package com.xiaoyv404.mirai.app.minecraftServer

import com.google.gson.Gson
import com.xiaoyv404.mirai.PluginConfig
import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.app.fsh.NfOptions
import com.xiaoyv404.mirai.app.minecraftServer.api.PlanPlayer
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.core.gid
import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.dao.*
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayer
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayerQQMapping
import com.xiaoyv404.mirai.model.mincraftServer.Permissions
import com.xiaoyv404.mirai.tool.ClientUtils
import net.mamoe.mirai.event.events.MessageEvent
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@App
class ISU : NfApp(), IFshApp {
    override fun getAppName() = "ISU"

    override fun getVersion() = "1.0.1"

    override fun getAppDescription() = "我的世界玩家状态监控"

    override fun getCommands() = arrayOf("-玩家状态", "-有妖怪在线吗", "-桃呢", "-有无妖怪", "-有妖怪在线吗")

    override fun getOptions() = NfOptions().apply {
        addOption("m", "more", false, "获取更多信息")
    }

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        val cmdLine = IFshApp.cmdLine(getOptions(), args)

        return when (args[0]) {
            "-玩家状态", "-桃呢" -> playerStatus(args, msg, cmdLine.hasOption("more"))
            "-有妖怪在线吗", "-有无妖怪", "-有妖怪在吗" -> findOP(msg)
            else -> false
        }
    }

    val regex = Regex("\\w+")

    private suspend fun playerStatus(args: Array<String>, msg: MessageEvent, more: Boolean): Boolean {
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
                this.lastLoginServer = "gtnh"
            }.findByNameAndNotEqServer()

        if (player == null) {
            msg.reply("无数据")
            return false
        }

        if (args.getOrNull(1) == null && args[0] != "-桃呢" && !personCheckAndSave(player, msg.uid())) {
            msg.reply("敲，有人在假冒${player.name}")
            return true
        }

        if (more && (MinecraftServerPlayerQQMapping { qq = msg.uid() }.getPermissionByQQ()
                ?: Permissions.Default) < Permissions.OP
        ) {
            msg.reply("需要权限至少为${Permissions.OP.permissionName}", true)
            return false
        }


        val replay = if (more)
            isOnlineAndMoreInfo(player) ?: run {
                msg.reply("API错误", true)
                return false
            }
        else
            isOnline(player)

        msg.reply(replay)

        return true
    }

    private fun personCheckAndSave(player: MinecraftServerPlayer, uid: Long): Boolean {
        val mapping = MinecraftServerPlayerQQMapping {
            this.playerName = player.name
        }.findByPlayerName().find {
            it.lock
        }

        if (mapping != null)
            return uid == mapping.qq


        MinecraftServerPlayerQQMapping {
            this.qq = uid
            this.playerName = player.name
            this.lock = false
        }.save()
        return true
    }

    private fun isOnline(player: MinecraftServerPlayer) =
        """
                名字: ${player.name}
                ${isUserOnline(player.lastLoginTime)}
                最后在线时间: ${player.lastLoginTime}
                服务器: ${player.lastLoginServer}
                UUID: ${player.id}
                身份: ${player.permissions.permissionName}
            """.trimIndent()

    private fun isUserOnline(lastLoginTime: LocalDateTime): String {
        val now = LocalDateTime.now()
        val duration = Duration.between(lastLoginTime, now)

        return if (duration.toMinutes() > 4) "不在线" else "在线"
    }

    private suspend fun isOnlineAndMoreInfo(player: MinecraftServerPlayer): String? {
        fun Long.toLocalDateTime() = Instant.ofEpochMilli(this).run {
            atZone(ZoneId.systemDefault()).toLocalDateTime()
        }

        val data = try {
            Gson().fromJson(
                ClientUtils.get<String>(
                    "${PluginConfig.etc.planApiUrl}/player/${player.id}/raw?server=Minecraft幻想乡"
                ), PlanPlayer::class.java
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } ?: return null

        val lastLoginTime = data.lastSeen.toLocalDateTime()
        val registerTime = data.registered.toLocalDateTime()

        return """
                名字: ${player.name}    ${isUserOnline(lastLoginTime)}
                最后在线时间: $lastLoginTime
                注册时间: $registerTime
                kick计数: ${data.kick_count}    死亡计数: ${data.death_count}
                击杀玩家: ${data.player_kill_count}    击杀怪物: ${data.mob_kill_count}
                OP: ${data.operator}    Baned: ${data.banned}
                服务器: ${player.lastLoginServer}
                UUID: ${player.id}
                身份: ${player.permissions.permissionName}
        """.trimIndent()
    }


    private suspend fun findOP(msg: MessageEvent): Boolean {
        val op = MinecraftServerPlayer().getAllOnlinePlayers().toMutableList()
        op.removeIf {
            it.permissions == Permissions.Default
        }
        if (op.isEmpty())
            msg.reply("没有呢 :(")
        else
            msg.reply("有: ${op.joinToString(", ") { it.name }}")

        return true
    }
}