package com.xiaoyv404.mirai

import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.core.NfApplicationManager
import com.xiaoyv404.mirai.core.NfClassFinder
import com.xiaoyv404.mirai.database.Database.connect
import com.xiaoyv404.mirai.tool.ClientUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.utils.info

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.xiaoyv404.ComprehensivePlugin",
        name = "ComprehensivePlugin",
        version = "1.0.1"
    )
) {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onEnable() {
        PluginData.reload()
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
        PluginData.save()
        ClientUtils.uninit()
        NfApplicationManager.nfApps.forEach {
            it.uninit()
        }
    }
}