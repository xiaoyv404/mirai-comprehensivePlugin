package com.xiaoyv404.mirai.app.ero.sauceNao

import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.core.gid
import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.databace.dao.authorityIdentification
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.time
import net.mamoe.mirai.message.nextMessage

@App
class SauceNaoImgSearch : NfApp(), IFshApp {
    override fun getAppName() = "SauceNaoImgSearch"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "SauceNaoͼƬ������"
    override fun getCommands() = arrayOf("��ͼ", "-img")

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        if (args[0] == "img" && !args[1].startsWith("search")) {
            return true
        }

        if (authorityIdentification(msg.uid(), msg.gid(), "SauceNaoSearch")) {
            val subject = msg.subject

            val sauceNao = SauceNaoRequester(msg)
            val image = msg.message[Image]
            if (image == null) {
                msg.reply("û��ͼƬ��˵,����60s�ڷ���ͼƬ",true)
                val nextMsg = msg.nextMessage()
                //�жϷ��͵�ʱ��
                if (nextMsg.time - msg.time < 60) {
                    val nextImage = nextMsg[Image]
                    if (nextImage == null) {
                        subject.sendMessage(nextMsg.quote() + "û�л�ȡͼƬ")
                    } else {
                        sauceNao.search(nextImage)
                        sauceNao.sendResult()
                    }
                }
            } else {
                sauceNao.search(image)
                sauceNao.sendResult()
            }
        }
        return true
    }
}