package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.dao.*
import com.xiaoyv404.mirai.databace.entity.mincraftServer.*
import com.xiaoyv404.mirai.entity.mincraftServer.*
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import org.apache.commons.cli.*

@App
class MinecraftServerList : NfApp(), IFshApp {
    override fun getAppName() = "MinecraftServerList"
    override fun getVersion() = "1.0.2"
    override fun getAppDescription() = "我的世界服务器状态监测-列表"
    override fun getCommands() =
        arrayOf(
            "-土豆列表",
            "-服务器列表"
        )

    private val options = Options().apply {
        addOption("p", "player", false, "获取玩家列表")
    }

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        val cmdLine = IFshApp.cmdLine(options, args)

        val list = MinecraftServer().toList()
        val img = MinecraftDataImgGenerator().drawList(list)
        msg.reply(msg.subject.uploadImage(img).toMessageChain())

        if (!cmdLine.hasOption("player"))
            return true
        MinecraftServerPlayer().getAllOnlinePlayers().send(msg)
        return true
    }

}
