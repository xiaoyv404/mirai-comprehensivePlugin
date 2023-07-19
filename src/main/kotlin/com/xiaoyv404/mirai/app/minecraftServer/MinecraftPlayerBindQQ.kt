package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.dao.groupType
import com.xiaoyv404.mirai.dao.isNotAdmin
import com.xiaoyv404.mirai.dao.save
import com.xiaoyv404.mirai.model.GroupType
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayerQQMapping
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.MessageEvent

@App
class MinecraftPlayerBindQQ : NfApp(), IFshApp {
    override fun getAppName() = "MinecraftPlayerBindQQ"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "我的世界玩家绑定QQ"
    override fun getCommands() = arrayOf("-McBindQQ")

    private val regex = Regex("\\d+")
    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        if (msg.isNotAdmin())
            return false
        if (msg.groupType() != GroupType.MCG)
            return false
        if (args[1] == "all") {
            return bindAll(msg)
        }
        return false
    }

    private fun bindAll(msg: MessageEvent): Boolean {
        val group = msg.subject as Group
        val data = group.members
            .filter { it.nameCard.isNotEmpty() }
            .mapNotNull { member -> regex.find(member.nameCard)?.let { member.id to it.value } }
            .toMap()
        data.forEach {
            MinecraftServerPlayerQQMapping {
                this.playerName = it.value
                this.qq = it.key
            }.save()
            log.info("bind ${it.value} to ${it.key}")
        }
        log.info("成功绑定 ${data.size} 个，失败 ${group.members.size - data.size}")
        return true
    }
}