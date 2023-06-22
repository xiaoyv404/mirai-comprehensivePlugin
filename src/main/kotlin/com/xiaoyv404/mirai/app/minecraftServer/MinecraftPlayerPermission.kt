package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.dao.authorityIdentification
import com.xiaoyv404.mirai.dao.findByName
import com.xiaoyv404.mirai.dao.update
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayer
import com.xiaoyv404.mirai.model.mincraftServer.Permissions
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.nextMessage

@App
class MinecraftPlayerPermission : NfApp(), IFshApp {
    override fun getAppName() = "MinecraftPlayerPermission"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "我的世界玩家权限"
    override fun getCommands() =
        arrayOf(
            "-UpdatePermission",
            "-更新权限"
        )

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        if (msg.authorityIdentification(
                "MinecraftServerPlayerPermission"
            )
        )
            return false

        msg.reply("请发送要更新的玩家名称，一行一个")
        updatePermission(msg, args.getOrNull(1) ?: return false)
        return true
    }

    private suspend fun updatePermission(msg: MessageEvent, permissionName: String) {
        val players = Regex(".+").findAll(msg.nextMessage().contentToString())
        val notfoundPlayers = mutableListOf<String>()
        val permission = Permissions.values().find {
            it.permissionName == permissionName
        }

        if (permission == null) {
            msg.reply("未知权限", true)
            return
        }

        players.forEach {
            val player = MinecraftServerPlayer {
                this.name = it.value
            }.findByName()

            if (player == null) {
                notfoundPlayers.add(it.value)
                return@forEach
            }

            MinecraftServerPlayer {
                this.id = player.id
                this.permissions = permission
            }.update()
        }

        if (notfoundPlayers.isEmpty())
            msg.reply("更新完成")
        else {
            msg.reply(
                "未找到: ${notfoundPlayers.joinToString("，")}"
            )
        }

    }
}