package com.xiaoyv404.mirai

import com.xiaoyv404.mirai.app.bilibili.b23ShortLinkEntrance
import com.xiaoyv404.mirai.app.bilibili.biliVideoEntrance
import com.xiaoyv404.mirai.app.bilibili.informationEntrance
import com.xiaoyv404.mirai.app.dice.Dice
import com.xiaoyv404.mirai.app.someThinkEntrance
import com.xiaoyv404.mirai.app.webAPI.WebApi
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.core.NfApplicationManager
import com.xiaoyv404.mirai.databace.Database.connect
import com.xiaoyv404.mirai.tool.KtorUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.utils.info
import org.reflections.Reflections


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
    @OptIn(DelicateCoroutinesApi::class)
    override fun onEnable() {
        PluginConfig.reload()
        connect()

        b23ShortLinkEntrance()
        biliVideoEntrance()
        informationEntrance()


        someThinkEntrance()
        Dice.entrance()

        WebApi.entrance()

        val f = Reflections("com.xiaoyv404.mirai.app")
        val set: Set<Class<*>> = f.getTypesAnnotatedWith(App::class.java)
        set.forEach {
            val bean = it.getDeclaredConstructor().newInstance()
            NfApplicationManager.appInitialization(bean as NfApp)
        }

        logger.info { "综合插件加载完成，版本：$version Java版本:${System.getProperty("java.version")}" }
    }
    override fun onDisable() {
        // 关闭ktor客户端, 防止堵塞线程无法关闭
        KtorUtils.closeClient()
    }
}