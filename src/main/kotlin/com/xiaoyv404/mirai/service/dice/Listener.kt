package com.xiaoyv404.mirai.service.dice

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Command
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeMessages

object Dice {
    fun entrance() {
        GlobalEventChannel.subscribeMessages {
            finding(Command.dice) {
                val rd = it.groups
                val start = rd[4]?.value?.toLong() ?: 0
                val end = if (rd[5] != null) {
                    if (rd[6] != null) {
                        rd[7]!!.value.toLong()
                    } else
                        rd[9]!!.value.toLong()
                } else
                    6L

                if (start == end){
                    subject.sendMessage("有什么意义呢，恼")
                    return@finding
                }

                val result = if (start > end) {
                    (end..start).random()
                } else
                    (start..end).random()

                PluginMain.logger.info(
                    """
                    start: $start
                    end: $end
                """.trimIndent()
                )
                subject.sendMessage("result: $result")
            }
        }
    }


}