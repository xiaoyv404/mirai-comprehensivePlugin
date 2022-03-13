package com.xiaoyv404.mirai.core

import net.mamoe.mirai.event.events.MessageEvent

abstract class NfApp {

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
    open fun getLimitCount(): Int = 0

    /**
     * 限制调用计次过期时间(秒)
     */
    open fun getLimitExpiresTime(): Long = 0

    /**
     * 应用使用说明
     */
    open fun getAppUsage(): String? = null

    /**
     * 应用初始化
     * 在应用被禁用时不会被调用
     */
    open fun init() {
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
        replyOnLimited: Boolean = true,
        block: suspend () -> Unit
    ) {
        block()

    }

    /**
     * 提交限制调用
     *
     * @param caller 调用者
     * @param place 调用地点
     */
    fun submitCallLimiter(caller: Long, place: Long) {
//        val key = "${getAppName()}_${place}_${caller}"
//        template.opsForValue().increment(key)
//        template.expire(key, getLimitExpiresTime(), TimeUnit.SECONDS)
    }

}