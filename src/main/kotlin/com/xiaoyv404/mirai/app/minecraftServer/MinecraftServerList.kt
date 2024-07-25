package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.app.fsh.NfOptions
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.dao.getAllOnlinePlayers
import com.xiaoyv404.mirai.dao.send
import com.xiaoyv404.mirai.dao.toList
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServer
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayer
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.toMessageChain
import org.apache.commons.cli.Options

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

    override fun getOptions(): Options = NfOptions().apply {
        addOption("p", "player", false, "获取玩家列表")
    }

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        val cmdLine = IFshApp.cmdLine(getOptions(), args)

        val list = MinecraftServer().toList().filter { !it.hilde }
        val img = MinecraftServerListGenerator().drawList(list)
        msg.reply(msg.subject.uploadImage(img).toMessageChain())


        if (!cmdLine.hasOption("player"))
            return true
        MinecraftServerPlayer{}.getAllOnlinePlayers().send(msg)
        return true
    }

}
