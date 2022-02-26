package com.xiaoyv404.mirai.service.ero.sauceNao

import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.databace.dao.itNotBot
import com.xiaoyv404.mirai.service.accessControl.authorityIdentification
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.time
import net.mamoe.mirai.message.nextMessage

fun searchListenerRegister() {
    GlobalEventChannel.subscribeMessages {
        finding(Command.SauceNao) {
            if ((authorityIdentification(
                    sender.id,
                    subject.id,
                    "SauceNaoSearch"
                )) && sender.itNotBot()) {
                val rd = it.groups
                if (rd[4]?.value == "-h" || rd[4]?.value == "--help") {
                    subject.sendMessage("help")
                } else {
                    val sauceNao = SauceNaoRequester(subject)
                    val image = message[Image]
                    if (image == null) {
                        subject.sendMessage(message.quote() + "û��ͼƬ��˵,����60s�ڷ���ͼƬ")
                        val nextMsg = nextMessage()
                        //�жϷ��͵�ʱ��
                        if (nextMsg.time - time < 60) {
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
            }
        }
    }
}