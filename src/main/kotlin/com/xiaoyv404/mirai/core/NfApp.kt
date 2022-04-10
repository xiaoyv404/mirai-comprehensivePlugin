package com.xiaoyv404.mirai.core

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.databace.Database
import net.mamoe.mirai.event.events.MessageEvent
import java.util.concurrent.TimeUnit


abstract class NfApp {

    val rdb = Database.rdb

    val log = PluginMain.logger

    /**
     * 应用名称
     */
    abstract fun getAppName(): String

    /**
     * 应用描述
     */
    open fun getAppDescription(): String? = null

    /**
     * 应用版本
     */
    abstract fun getVersion(): String

    /**
     * 一定时间内限制调用次数
     */
    open fun getLimitCount(): Int = 1000

    /**
     * 限制调用计次过期时间(秒)
     */
    open fun getLimitExpiresTime(): Long = 60

    /**
     * 超过限制调用提示语开关
     */
    open fun getLimitHint(): Boolean = true

    /**
     * 应用使用说明
     */
    open fun getAppUsage(): String? = null

    /**
     * 应用初始化
     * 在应用被禁用时，不会被调用
     */
    open fun init() {
    }

    /**
     * 应用关闭
     * 在应用被禁用时，不会被调用
     */
    open fun uninit() {
    }

    /**
     * 取限制调用剩余次数
     *
     * @param caller 调用者
     * @param place 调用地点
     * @param app 限制的应用名 默认是当前应用
     * @return 当前剩余可调用次数
     */
    fun getCallLimiterRemainCount(caller: Long, place: Long, app: String = getAppName()): Int {
        val key = "${app}_${place}_${caller}"
        val remain = rdb.get(key).get(1,TimeUnit.MINUTES)
        return if (remain == null) {
            getLimitCount()
        } else {
            getLimitCount() - remain.toInt()
        }
    }

    /**
     * 检查限制调用，成功后执行，否则发送提示消息并计次
     *
     * @param caller 调用者
     * @param place 调用地点
     */
    suspend fun requireCallLimiter(
        msg: MessageEvent,
        caller: Long,
        place: Long,
        replyOnLimited: Boolean,
        block: suspend () -> Unit
    ) {
        val remainCount = getCallLimiterRemainCount(caller, place)
        when {
            remainCount > 0 -> block()
            remainCount > -2 -> {
                if (replyOnLimited) {
                    msg.reply("404好累惹qwq", quote = true)
                }
                submitCallLimiter(caller, place)
            }
            remainCount > -3 -> {
                if (replyOnLimited) {
                    msg.reply( "去死啊", quote = true)
                }
                submitCallLimiter(caller, place)
            }
        }
    }

    /**
     * 提交限制调用
     *
     * @param caller 调用者
     * @param place 调用地点
     */
    fun submitCallLimiter(caller: Long, place: Long) {
        val key = "${getAppName()}_${place}_${caller}"
        val num = rdb.get(key).get(1,TimeUnit.MINUTES)?.toIntOrNull() ?:0

        rdb.setex(key,getLimitExpiresTime(),(num+1).toString()) }
}