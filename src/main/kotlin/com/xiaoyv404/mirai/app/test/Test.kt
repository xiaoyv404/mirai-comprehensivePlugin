package com.xiaoyv404.mirai.app.test

import com.google.gson.*
import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.tool.*
import net.mamoe.mirai.event.events.*

@App
class Test : NfApp(), IFshApp {
    override fun getAppName() = "Test"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "测试模块"
    override fun getCommands() =
        arrayOf(
            "-ServerTps",
            "-测试",
            "-test"
        )

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        val serverOverview = Gson().fromJson(
            ClientUtils.get<String>(
                "http://mc.touhou.site:8848/v1/serverOverview" +
                    "server=Minecraft幻想乡"
            ), ServerOverview::class.java
        )

        msg.reply("""
            time: ${serverOverview.timestamp_f},
            过去7天的数据
            下线时间: ${serverOverview.last_7_days.downtime}
            平均tps: ${serverOverview.last_7_days.average_tps}
            这一周你们鲨了: ${serverOverview.weeks.mob_kills_trend.text}只怪
        """.trimIndent())
        return true
    }
}