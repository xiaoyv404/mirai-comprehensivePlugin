package com.xiaoyv404.mirai.service.ero.localGallery

import com.xiaoyv404.mirai.PluginConfig
import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.service.accessControl.authorityIdentification
import com.xiaoyv404.mirai.service.ero.*
import com.xiaoyv404.mirai.service.getUserInformation
import com.xiaoyv404.mirai.service.tool.KtorUtils.normalClient
import io.ktor.client.request.*
import io.ktor.util.*
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.File
import java.io.InputStream


@KtorExperimentalAPI
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
                    0 -> subject.sendMessage("w?你到底想让404干什么呢, 喵")
                    9 -> subject.sendMessage("9?是这个⑨吗?www")
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
                if (rd[3]!!.value == "-h" || rd[3]!!.value == "--help")
                    subject.sendMessage(
                        "Usage: 添加涩图 [option] <target>\n" +
                            "根据PixivID添加至图库\n" +
                            "option: \n" +
                            "   -h  帮助"
                    )
                else {
                    try {
                        val ii = unformat(rd[3]!!.value, sender.id)
                        if (ii.picturesNum == 1) {
                            subject.sendMessage(
                                File("${PluginConfig.database.SaveAddress}${ii.id}.${ii.extension}")
                                    .uploadAsImage(subject).plus(sequenceInformation(ii))
                            )
                        } else {
                            subject.sendMessage(
                                buildForwardMessage {
                                    bot.says(sequenceInformation(ii))
                                    for (i in 1..ii.picturesNum) {
                                        bot.says(
                                            File("${PluginConfig.database.SaveAddress}${ii.id}-$i.${ii.extension}")
                                                .uploadAsImage(subject)
                                        )
                                    }
                                }
                            )
                        }
                    } catch (e: Exception) {
                        PluginMain.logger.error(e)
                        subject.sendMessage("出错啦(详见控制台)")
                    }
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

                if (rd[3]!!.value == "-h" || rd[3]!!.value == "--help")
                    subject.sendMessage(
                        "Usage：搜涩图 [option] <tag>\n" +
                            "根据tag从本地图库搜图\n" +
                            "option: \n" +
                            "   -h  帮助\n" +
                            "example: \n" +
                            "   \"搜涩图 香風智乃\""
                    )
                else {
                    val tagid = queryTagIdByTag(rd[3]!!.value)
                    if (tagid != -1L) {
                        val id = queryIdByTagId(tagid).random().toLong()
                        val ii = getImgInformationById(id)

                        if (ii.picturesNum == 1)
                            subject.sendMessage(
                                File("${PluginConfig.database.SaveAddress}${ii.id}.${ii.extension}")
                                    .uploadAsImage(subject).plus(sequenceInformation(ii))
                            )
                        else {
                            subject.sendMessage(
                                buildForwardMessage {
                                    bot.says(sequenceInformation(ii))
                                    for (i in 1..ii.picturesNum) {
                                        bot.says(
                                            File("${PluginConfig.database.SaveAddress}${ii.id}-$i${ii.extension}")
                                                .uploadAsImage(subject)
                                        )
                                    }
                                }
                            )
                        }
                    } else
                        subject.sendMessage("唔....似乎没有呢")
                }
            }
        }
        finding(Command.eroRemove) {
            if (getUserInformation(sender.id).admin == true) {
                val rd = it.groups
                if (rd[3]!!.value == "-h" || rd[3]!!.value == "--help")
                    subject.sendMessage(
                        "help"
                    )
                else {
                    val id = rd[3]!!.value.toLong()
                    subject.sendMessage("正在删除: $id")

                    val tags = queryTagIdById(id)

                    tags.forEach { tagid ->
                        val num = queryTagQuantityByTagId(tagid)
                        updateTagNumber(tagid, num - 1)
                    }
                    val imgNum = getImgInformationById(id).picturesNum
                    if (imgNum == 1)
                        File("${PluginConfig.database.SaveAddress}$id.png").deleteRecursively()
                    else
                        for (i in 1..imgNum) {
                            File("${PluginConfig.database.SaveAddress}$id-$i.png")
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
}