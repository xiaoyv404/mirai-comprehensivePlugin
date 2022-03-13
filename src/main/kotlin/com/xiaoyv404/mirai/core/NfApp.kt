package com.xiaoyv404.mirai.core

import net.mamoe.mirai.event.events.MessageEvent

abstract class NfApp {

    /**
     * Ӧ������
     */
    abstract fun getAppName(): String

    /**
     * Ӧ������
     */
    open fun getAppDescription(): String? = null

    /**
     * Ӧ�ð汾
     */
    abstract fun getVersion(): String

    /**
     * һ��ʱ�������Ƶ��ô���
     */
    open fun getLimitCount(): Int = 0

    /**
     * ���Ƶ��üƴι���ʱ��(��)
     */
    open fun getLimitExpiresTime(): Long = 0

    /**
     * Ӧ��ʹ��˵��
     */
    open fun getAppUsage(): String? = null

    /**
     * Ӧ�ó�ʼ��
     * ��Ӧ�ñ�����ʱ���ᱻ����
     */
    open fun init() {
    }

    /**
     * ������Ƶ��ã��ɹ���ִ�У���������ʾ��Ϣ���ƴ�
     *
     * @param caller ������
     * @param place ���õص�
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
     * �ύ���Ƶ���
     *
     * @param caller ������
     * @param place ���õص�
     */
    fun submitCallLimiter(caller: Long, place: Long) {
//        val key = "${getAppName()}_${place}_${caller}"
//        template.opsForValue().increment(key)
//        template.expire(key, getLimitExpiresTime(), TimeUnit.SECONDS)
    }

}