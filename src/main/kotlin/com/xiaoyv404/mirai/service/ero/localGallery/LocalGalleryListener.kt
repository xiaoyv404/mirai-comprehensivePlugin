package com.xiaoyv404.mirai.service.ero.localGallery

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.databace.dao.gallery.GalleryTag
import com.xiaoyv404.mirai.databace.dao.gallery.update
import com.xiaoyv404.mirai.service.accessControl.authorityIdentification
import com.xiaoyv404.mirai.service.ero.*
import com.xiaoyv404.mirai.service.getUserInformation
import com.xiaoyv404.mirai.service.tool.KtorUtils.normalClient
import io.ktor.client.request.*
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.nextMessage
import java.io.InputStream

fun localGalleryListener() {
    GlobalEventChannel.subscribeMessages {
        finding(Command.ero) {
            if ((authorityIdentification(
                    sender.id,
                    subject.id,
                    "NetworkEro"
                )) && (getUserInformation(sender.id).bot != true)
            ) {
                var num = it.groups[3]!!.value.toInt()
                when (num) {
                    0      -> subject.sendMessage("w?你到底想让404干什么呢, 喵")
                    9      -> subject.sendMessage("9?是这个⑨吗?www")
                    114514 -> subject.sendMessage("好臭啊啊啊啊")
                }
                if (num > 5 && getUserInformation(sender.id).setu != true) {
                    num = if (9 == (5..10).random()) {
                        subject.sendMessage("去死啊你这个变态, 要看自己去Pixiv看")
                        0
                    } else {
                        5
                    }
                }
                if (num != 0)
                    subject.sendMessage("少女祈祷中...")

                for (i in 1..num) {
                    val im = normalClient.get<InputStream?>(setuAPIUrl)
                    if (im != null)
                        subject.sendImage(im)
                    else
                        subject.sendMessage("`(*>﹏<*)′服务器酱好像不理我惹")
                }
            }
        }
        finding(Command.eroAdd) {
            if ((authorityIdentification(
                    sender.id,
                    subject.id,
                    "LocalGallery"
                )) && (getUserInformation(sender.id).bot != true)
            ) {
                val rd = it.groups
                val ids = Regex("\\d+").findAll(
                    if (rd[3] == null) {
                        subject.sendMessage("没找到图片ID捏，请发送图片ID")
                        nextMessage().contentToString()
                    } else
                        rd[3]!!.value
                ).toList()
                val noOutPut = rd[7]!=null
                PluginMain.logger.info("找到${ids.size}个ID")

                ids.forEach { id ->
                    PluginMain.logger.info("下载${id.value}")
                    if (LocalGallery(subject).unformat(id.value, sender.id, noOutPut)) {
                        subject.sendMessage("出错啦(详见控制台)")
                    }
                }

                if (ids.size >= 5){
                    subject.sendMessage("完成啦w!")
                }
            }
        }
        finding(Command.eroSearch) {
            if ((authorityIdentification(
                    sender.id,
                    subject.id,
                    "LocalGallery"
                )) && (getUserInformation(sender.id).bot != true)
            ) {
                val rd = it.groups
                val tagid = queryTagIdByTag(rd[3]!!.value)
                if (tagid == null) {
                    subject.sendMessage("唔....似乎没有呢")
                    return@finding
                }

                val id = queryIdByTagId(tagid).random().toLong()
                val ii = getImgInformationById(id)
                LocalGallery(subject).send(ii)
            }
        }
        finding(Command.eroRemove) {
            if (getUserInformation(sender.id).admin == true) {
                val rd = it.groups
                val id = rd[3]!!.value.toLong()
                subject.sendMessage("正在删除: $id")

                val tags = queryTagIdById(id)

                tags.forEach { tagidA ->
                    val numA = queryTagQuantityByTagId(tagidA)
                    GalleryTag{
                        tagid = tagidA
                        num = numA
                    }.update()
                }

                val information = getImgInformationById(id)
                val imgNum = information.picturesNum
                val extension = information.extension

                if (imgNum == 1)
                    PluginMain.resolveDataFile("gallery/$id.$extension").deleteRecursively()
                else
                    for (i in 1..imgNum) {
                        PluginMain.resolveDataFile("gallery/$id-$i.$extension")
                            .deleteRecursively()
                    }
                removeInformationById(id)

                subject.sendMessage(
                    "${id}已删除\n" +
                        "删除${imgNum}张图片    ${tags.size + 1}条记录"
                )
            }
        }
    }
}


