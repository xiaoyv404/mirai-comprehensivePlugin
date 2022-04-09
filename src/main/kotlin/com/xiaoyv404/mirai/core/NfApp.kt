package com.xiaoyv404.mirai.core

import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.databace.Database
import net.mamoe.mirai.event.events.MessageEvent


abstract class NfApp {

    private val rdb = Database.rdb

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
    open fun getLimitCount(): Int = 1000

    /**
     * ���Ƶ��üƴι���ʱ��(��)
     */
    open fun getLimitExpiresTime(): Long = 60

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
     * ȡ���Ƶ���ʣ�����
     *
     * @param caller ������
     * @param place ���õص�
     * @param app ���Ƶ�Ӧ���� Ĭ���ǵ�ǰӦ��
     * @return ��ǰʣ��ɵ��ô���
     */
    fun getCallLimiterRemainCount(caller: Long, place: Long, app: String = getAppName()): Int {
        val key = "${app}_${place}_${caller}"
        val remain = rdb.sync().get(key)
        return if (remain == null) {
            getLimitCount()
        } else {
            getLimitCount() - remain.toInt()
        }
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
        val remainCount = getCallLimiterRemainCount(caller, place)
        when {
            remainCount > 0 -> block()
            remainCount > -2 -> {
                println(remainCount)
                if (replyOnLimited) {
                    msg.reply("404������qwq", quote = true)
                }
                submitCallLimiter(caller, place)
            }
            remainCount > -3 -> {
                if (replyOnLimited) {
                    msg.reply( "ȥ����", quote = true)
                }
                submitCallLimiter(caller, place)
            }
        }
    }

    /**
     * �ύ���Ƶ���
     *
     * @param caller ������
     * @param place ���õص�
     */
    fun submitCallLimiter(caller: Long, place: Long) {
        val key = "${getAppName()}_${place}_${caller}"
        val num = rdb.sync().get(key)?.toIntOrNull() ?:0

        rdb.async().setex(key,getLimitExpiresTime(),(num+1).toString()) }
}