package com.xiaoyv404.mirai

import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.databace.Database.connect
import com.xiaoyv404.mirai.tool.*
import kotlinx.coroutines.*
import net.mamoe.mirai.console.plugin.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.utils.*

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.xiaoyv404.ComprehensivePlugin",
        name = "ComprehensivePlugin",
        version = "1.0.1"
    )
) {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onEnable() {
        NfPluginData.reload()
        PluginConfig.reload()
        connect()

        val set: Set<Class<*>> = NfClassFinder().getAnnotationClasses(
            "com.xiaoyv404.mirai.app",
            App::class.java,
            PluginMain.jvmPluginClasspath.pluginFile
        )

        set.forEach {
            val bean = it.getDeclaredConstructor().newInstance()
            NfApplicationManager.appInitialization(bean as NfApp)
        }

        logger.info { "综合插件加载完成，版本：$version Java版本:${System.getProperty("java.version")}" }
    }

    override fun onDisable() {
        NfPluginData.save()
        ClientUtils.uninit()
        NfApplicationManager.nfApps.forEach {
            it.uninit()
        }
    }
}