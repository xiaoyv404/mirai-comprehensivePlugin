package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.dao.groupType
import com.xiaoyv404.mirai.dao.isNotAdmin
import com.xiaoyv404.mirai.dao.save
import com.xiaoyv404.mirai.model.GroupType
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayerQQMapping
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.buildMessageChain

@App
class MinecraftPlayerBindQQ : NfApp(), IFshApp {
    override fun getAppName() = "MinecraftPlayerBindQQ"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "我的世界玩家绑定QQ"
    override fun getCommands() = arrayOf("-McBindQQ")

    private val regex = Regex("\\w+")
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

    private suspend fun bindAll(msg: MessageEvent): Boolean {
        val group = msg.subject as Group
        val data = group.members
            .filter { it.nameCard.isNotEmpty() }
            .mapNotNull { member -> regex.find(member.nameCard)?.let { member.id to it.value } }
            .toMap()

        val duplicateList = mutableMapOf<String, HashSet<Long>>()
        data.forEach {
            val storedData = MinecraftServerPlayerQQMapping {
                this.playerName = it.value
                this.qq = it.key
            }.save()
            when {
                storedData == null -> log.info("bind ${it.value} to ${it.key}")
                storedData.qq != it.key -> {
                    log.info("playerName Duplicate ${storedData.qq} and ${it.key}")
                    duplicateList.getOrPut(it.value) {
                        hashSetOf(it.key, storedData.qq)
                    }.add(it.key)
                }
            }
        }
        log.info("成功绑定 ${data.size} 个")
        val replay = buildMessageChain {
            +"存在相同玩家名称："
            duplicateList.forEach {
                +"\n${it.value.joinToString { ", " }} 为 ${it.key}"
            }
        }
        msg.reply(replay, true)
        return true
    }
}