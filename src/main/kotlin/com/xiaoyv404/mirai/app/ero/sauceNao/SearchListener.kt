package com.xiaoyv404.mirai.app.ero.sauceNao

import com.xiaoyv404.mirai.app.fsh.*
import com.xiaoyv404.mirai.core.*
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.dao.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote

@App
class SauceNaoImgSearch : NfApp(), IFshApp {
    override fun getAppName() = "SauceNaoImgSearch"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "SauceNao图片搜索器"
    override fun getCommands() = arrayOf("搜图", "-img")

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        if (args[0] == "img" && !args[1].startsWith("search")) {
            return false
        }

        if (msg.authorityIdentification("SauceNaoSearch"))
            return false

        val subject = msg.subject

        val sauceNao = SauceNaoRequester(msg)
        val image = msg.message[Image]
        if (image == null) {
            msg.reply("没有图片的说,请在60s内发送图片", true)
            val nextMsg = msg.nextMessage()
            //判断发送的时间
            if (nextMsg.time - msg.time < 60) {
                val nextImage = nextMsg[Image]
                if (nextImage == null) {
                    subject.sendMessage(nextMsg.quote() + "没有获取图片")
                } else {
                    sauceNao.search(nextImage)
                    sauceNao.sendResult()
                }
            }
        } else {
            sauceNao.search(image)
            sauceNao.sendResult()
        }

        return true
    }
}