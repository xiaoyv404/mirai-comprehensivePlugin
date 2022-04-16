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
     * �������Ƶ�����ʾ�￪��
     */
    open fun getLimitHint(): Boolean = true

    /**
     * Ӧ��ʹ��˵��
     */
    open fun getAppUsage(): String? = null

    /**
     * Ӧ�ó�ʼ��
     * ��Ӧ�ñ�����ʱ�����ᱻ����
     */
    open fun init() {
    }

    /**
     * Ӧ�ùر�
     * ��Ӧ�ñ�����ʱ�����ᱻ����
     */
    open fun uninit() {
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
        val remain = rdb.get(key).get(1,TimeUnit.MINUTES)
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
        replyOnLimited: Boolean,
        block: suspend () -> Unit
    ) {
        val remainCount = getCallLimiterRemainCount(caller, place)
        when {
            remainCount > 0 -> block()
            remainCount > -2 -> {
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
        val num = rdb.get(key).get(1,TimeUnit.MINUTES)?.toIntOrNull() ?:0

        rdb.setex(key,getLimitExpiresTime(),(num+1).toString()) }
}