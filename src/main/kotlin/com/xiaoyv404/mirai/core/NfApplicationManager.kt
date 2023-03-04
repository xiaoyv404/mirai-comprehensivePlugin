package com.xiaoyv404.mirai.core

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.app.groupHelper.*
import kotlinx.coroutines.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*

object NfApplicationManager {
    private val log = PluginMain.logger
    val nfApps: MutableSet<NfApp> = HashSet()
    val fshCommands = HashMap<String, IFshApp>()

    private val appBlacklist = HashSet<String>()

    @DelicateCoroutinesApi
    fun appInitialization(app: NfApp) {
        nfApps.add(app)
        val name = app.getAppName()
        if (appBlacklist.contains(name)) {
            log.info("跳过应用$name@${app.getVersion()}: 在加载黑名单")
            return
        }
        log.info("注册应用$name@${app.getVersion()}")
        app.init()
        when (app) {
            is NfAppMessageHandler -> {
                GlobalEventChannel.subscribeAlways(MessageEvent::class.java) {
                    GlobalScope.launch {
                        app.handleMessage(it)
                    }
                }
                log.info("注册消息处理器${app.getAppName()}")
            }

            is NfAppMessageRecallHandler -> {
                GlobalEventChannel.subscribeAlways(MessageRecallEvent::class.java) {
                    GlobalScope.launch {
                        app.handleMessage(it)
                    }
                }
                log.info("注册撤回消息处理器${app.getAppName()}")
            }

            is NfAppMemberJoinRequestHandler -> {
                GlobalEventChannel.subscribeAlways(MemberJoinRequestEvent::class.java) {
                    GlobalScope.launch {
                        app.handleMessage(it)
                    }
                }
                log.info("注册加入事件处理器${app.getAppName()}")
            }

            is IFshApp -> {
                for (command in app.getCommands()) {
                    fshCommands[command] = app
                    log.info("注册fsh命令$command")
                }
            }
        }
    }
}