package com.xiaoyv404.mirai

import com.xiaoyv404.mirai.databace.Database.connect
import com.xiaoyv404.mirai.service.bilibili.b23ShortLinkEntrance
import com.xiaoyv404.mirai.service.bilibili.biliVideoEntrance
import com.xiaoyv404.mirai.service.history.save
import com.xiaoyv404.mirai.service.setu.setuEntrance
import com.xiaoyv404.mirai.service.someThinkEntrance
import com.xiaoyv404.mirai.service.thesaurus.thesaurusEntrance
import io.ktor.util.*
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.utils.MiraiInternalApi
import net.mamoe.mirai.utils.info

object Version {
    const val ID = "com.xiaoyv404.ComprehensivePlugin"
    const val NAME = "ComprehensivePlugin"
    const val PLUGINVERSION = "0.3.0"
}

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = Version.ID,
        name = Version.NAME,
        version = Version.PLUGINVERSION
    )
) {
    @MiraiInternalApi
    @KtorExperimentalAPI
    override fun onEnable() {
        PluginConfig.reload()

        logger.info { "综合插件加载完成，版本：$version Java版本:${System.getProperty("java.version")}" }

        connect()

        b23ShortLinkEntrance()
        biliVideoEntrance()

        setuEntrance()
        thesaurusEntrance()

        someThinkEntrance()

        save()
    }
}