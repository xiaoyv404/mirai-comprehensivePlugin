package com.xiaoyv404.mirai

import com.xiaoyv404.mirai.app.bilibili.b23ShortLinkEntrance
import com.xiaoyv404.mirai.app.bilibili.biliVideoEntrance
import com.xiaoyv404.mirai.app.bilibili.informationEntrance
import com.xiaoyv404.mirai.app.dice.Dice
import com.xiaoyv404.mirai.app.ero.eroEntrance
import com.xiaoyv404.mirai.app.history.historyEntrance
import com.xiaoyv404.mirai.app.minecraftServer.minecraftServerEntrance
import com.xiaoyv404.mirai.app.someThinkEntrance
import com.xiaoyv404.mirai.app.thesaurus.thesaurusEntrance
import com.xiaoyv404.mirai.app.webAPI.WebApi
import com.xiaoyv404.mirai.databace.Database.connect
import com.xiaoyv404.mirai.tool.KtorUtils
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.utils.info

object Version {
    const val ID = "com.xiaoyv404.ComprehensivePlugin"
    const val NAME = "ComprehensivePlugin"
    const val PLUGINVERSION = "1.0.0"
}


object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = Version.ID,
        name = Version.NAME,
        version = Version.PLUGINVERSION
    )
)       {
    override fun onEnable() {
        PluginConfig.reload()

        logger.info { "综合插件加载完成，版本：$version Java版本:${System.getProperty("java.version")}" }

        connect()

        b23ShortLinkEntrance()
        biliVideoEntrance()
        informationEntrance()

        thesaurusEntrance()

        historyEntrance()

        eroEntrance()
        someThinkEntrance()
        Dice.entrance()
        minecraftServerEntrance()

        WebApi.entrance()
    }
    override fun onDisable() {
        // 关闭ktor客户端, 防止堵塞线程无法关闭
        KtorUtils.closeClient()
    }
}