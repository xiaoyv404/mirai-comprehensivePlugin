package com.xiaoyv404.mirai.app.minecraftServer

import com.google.gson.Gson
import com.xiaoyv404.mirai.PluginConfig
import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.dao.send
import com.xiaoyv404.mirai.dao.toList
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServer
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerPlayer
import com.xiaoyv404.mirai.tool.ClientUtils
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

    private val options = Options().apply {
        addOption("p", "player", false, "获取玩家列表")
    }

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        val cmdLine = IFshApp.cmdLine(options, args)

        val tps = try {
            Gson().fromJson(
                ClientUtils.get<String>(
                    "${PluginConfig.etc.planApiUrl}/v1/performanceOverview?server=Minecraft幻想乡"
                ), Performance::class.java
            ).tps.takeLast(720)
        } catch (e: Exception) {
            null
        }

        val low = mutableListOf<Long>()
        val average = mutableListOf<Long>()

        var lowi: Long = 20
        var averagei: Long = 0
        var k = 1
        tps?.forEach {
            if (k == 60) {
                low.add(lowi)
                average.add(averagei / 60)
                lowi = 20
                averagei = 0
                k = 0
            }
            if (lowi > it[1])
                lowi = it[1].toLong()
            k++
            averagei += it[1].toLong()
        }

        val list = MinecraftServer().toList()
        val img = MinecraftServerListGenerator().drawList(list, low, average)
        msg.reply(msg.subject.uploadImage(img).toMessageChain())

        if (!cmdLine.hasOption("player"))
            return true
        MinecraftServerPlayer().getAllOnlinePlayers().send(msg)
        return true
    }

}
