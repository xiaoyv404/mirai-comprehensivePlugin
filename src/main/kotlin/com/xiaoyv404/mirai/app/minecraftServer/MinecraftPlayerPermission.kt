package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.dao.*
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayer
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayerQQMapping
import com.xiaoyv404.mirai.model.mincraftServer.Permissions
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.nextMessage

@App
class MinecraftPlayerPermission : NfApp(), IFshApp {
    override fun getAppName() = "MinecraftPlayerPermission"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "我的世界玩家权限"
    override fun getCommands() =
        arrayOf(
            "-UpdatePermission",
            "-更新权限",
            "-我的世界玩家绑定qq",
            "-我的世界玩家取消绑定qq"
        )

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        if (msg.authorityIdentification(getAppName()))
            return false

        return when (args[0]) {
            "-UpdatePermission", "-更新权限" -> updatePermission(msg, args.getOrNull(1) ?: return false)
            "-我的世界玩家绑定qq" -> bindQQ(msg)
            "-我的世界玩家取消绑定qq" -> unbindQQ(msg, args.getOrNull(1)?.toLong() ?: return false)
            else -> return false
        }
    }

    private suspend fun unbindQQ(msg: MessageEvent, id: Long): Boolean {
        MinecraftServerPlayerQQMapping {
            this.qq = id
        }.findByQQId().let {
            if (it == null) {
                msg.reply("查无此人", true)
                return false
            }

            it.delete()
            msg.reply("取消绑定成功", true)
        }
        return true
    }

    private suspend fun bindQQ(msg: MessageEvent): Boolean {
        msg.reply(
            """
            请发送要更新的玩家名称与对应的qq，一行一个
            示例: xiaoyv_404 123
        """.trimIndent()
        )
        val players = Regex("[A-z,\\d]+ \\d+").findAll(msg.nextMessage().contentToString()).map {
            it.value.splitToSequence(" ").let {
                it.first() to it.last().toLong()
            }
        }.toMap()
        players.forEach {
            MinecraftServerPlayerQQMapping {
                this.playerName = it.key
            }.findByPlayerName().filter {
                !it.lock
            }.forEach {
                it.delete()
            }

            MinecraftServerPlayerQQMapping {
                this.playerName = it.key
                this.qq = it.value
                this.lock = true
            }.save()
        }
        msg.reply(buildMessageChain {
            +"更新成功，共更新${players.size}个\n"
            players.forEach {
                +"${it.key} to ${it.value}"
            }
        })
        return true
    }

    private suspend fun updatePermission(msg: MessageEvent, permissionName: String): Boolean {
        msg.reply("请发送要更新的玩家名称，一行一个")
        val players = Regex(".+").findAll(msg.nextMessage().contentToString())
        val notfoundPlayers = mutableListOf<String>()
        val permission = Permissions.values().find {
            it.permissionName == permissionName
        }

        if (permission == null) {
            msg.reply("未知权限", true)
            return false
        }

        players.forEach {
            val player = MinecraftServerPlayer {
                this.name = it.value
                this.lastLoginServer = "gtnh"
            }.findByNameAndNotEqServer()

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
        return true
    }
}