package com.xiaoyv404.mirai.core

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.app.fsh.IFshApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.events.MessageRecallEvent

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
            log.info("����Ӧ��$name@${app.getVersion()}: �ڼ��غ�����")
            return
        }
        log.info("ע��Ӧ��$name@${app.getVersion()}")
        app.init()
        if (app is NfAppMessageHandler) {
            GlobalEventChannel.subscribeAlways(MessageEvent::class.java) {
                GlobalScope.launch {
                    app.handleMessage(it)
                }
            }
            log.info("ע����Ϣ������${app.getAppName()}")
        }

        if (app is NfAppMessageRecallHandler){
            GlobalEventChannel.subscribeAlways(MessageRecallEvent::class.java){
                GlobalScope.launch {
                    app.handleMessage(it)
                }
            }
            log.info("ע�᳷����Ϣ������${app.getAppName()}")
        }
        if (app is IFshApp) {
            for (command in app.getCommands()) {
                fshCommands[command] = app
                log.info("ע��fsh����$command")
            }
        }
    }

}