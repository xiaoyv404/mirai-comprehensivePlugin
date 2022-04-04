package com.xiaoyv404.mirai.app.dice

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import net.mamoe.mirai.event.events.MessageEvent

@App
class Dice : NfApp(), IFshApp {
    override fun getAppName() = "Dice"
    override fun getVersion() = "1.0.1"
    override fun getAppDescription() = "骰子"
    override fun getCommands() = arrayOf(".r")

    private val log = PluginMain.logger

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        val rd = args.getOrNull(1)?.let { Regex("^((\\d+)((\\((\\d+)\\))|(:(\\d+)))?)\$").find(it) }?.groups

        val start = rd?.get(2)?.value?.toLong() ?: 1L
        val end = if (rd?.get(3) != null) {
            if (rd[4] != null) {
                rd[5]!!.value.toLong()
            } else
                rd[7]!!.value.toLong()
        } else
            6L

        if (start == end) {
            msg.reply("有什么意义呢，恼")
            return true
        }

        val result = if (start > end) {
            (end..start).random()
        } else
            (start..end).random()

        log.info(
            """
                    start: $start
                    end: $end
                """.trimIndent()
        )

        msg.reply("result: $result")

        return true
    }
}
