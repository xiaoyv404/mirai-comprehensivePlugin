package com.xiaoyv404.mirai.app.fmp

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.core.rgwMsgIdentity
import com.xiaoyv404.mirai.databace.Database
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.recallMessage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.message.data.MessageSourceBuilder
import net.mamoe.mirai.message.data.MessageSourceKind
import java.util.concurrent.TimeUnit

@App
class Fmp : NfApp() {
    override fun getAppName() = "fmp"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "命令系统的撤回事件实现"

    private val log = PluginMain.logger

    override fun init() {
        GlobalEventChannel.subscribeAlways(MessageRecallEvent::class.java) { onRecall(it) }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun onRecall(event: MessageRecallEvent) {
        val bot = event.bot
        Database.rdb.keys("fmp:replied:${event.rgwMsgIdentity()}:*").get(1, TimeUnit.MINUTES).forEach { key ->
            val sentIdentity = Database.rdb.get(key).get(1, TimeUnit.MINUTES)
            if (sentIdentity != null) {
                val split = sentIdentity.split("#")
                if (split.size == 5) {
                    val ids = ArrayList<Int>()
                    split[0].split(",").forEach { ids.add(it.toInt()) }
                    val internalIds = ArrayList<Int>()
                    split[1].split(",").forEach { internalIds.add(it.toInt()) }
                    val time = split[2].toInt()

                    val recallSource = MessageSourceBuilder()
                    recallSource.ids = ids.toIntArray()
                    recallSource.internalIds = internalIds.toIntArray()
                    recallSource.time = time
                    recallSource.fromId = split[3].toLong()
                    recallSource.targetId = split[4].toLong()

                    log.info("撤回触发过的消息$sentIdentity")
                    GlobalScope.launch {
                        if (event is MessageRecallEvent.GroupRecall) {
                            event.group.recallMessage(recallSource.build(bot.id, MessageSourceKind.GROUP))
                        } else if (event is MessageRecallEvent.FriendRecall) {
                            event.operator.recallMessage(recallSource.build(bot.id, MessageSourceKind.FRIEND))
                        }
                    }
                }
            }
        }
    }
}