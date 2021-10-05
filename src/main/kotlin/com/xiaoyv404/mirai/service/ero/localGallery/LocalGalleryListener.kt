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
                    0 -> subject.sendMessage("w?�㵽������404��ʲô��, ��")
                    9 -> subject.sendMessage("9?���������?www")
                    114514 -> subject.sendMessage("�ó���������")
                }
                if (num > 5 && getUserInformation(sender.id).setu != true) {
                    num = if (9 == (5..10).random()) {
                        subject.sendMessage("ȥ�����������̬, Ҫ���Լ�ȥPixiv��")
                        0
                    } else {
                        5
                    }
                }
                if (num != 0)
                    subject.sendMessage("��Ů����...")

                for (i in 1..num) {
                    val im = normalClient.get<InputStream?>(setuAPIUrl)
                    if (im != null)
                        subject.sendImage(im)
                    else
                        subject.sendMessage("`(*>�n<*)�������������������")
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
                        "Usage: ���ɬͼ [option] <target>\n" +
                            "����PixivID�����ͼ��\n" +
                            "option: \n" +
                            "   -h  ����"
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
                        subject.sendMessage("������(�������̨)")
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
                        "Usage����ɬͼ [option] <tag>\n" +
                            "����tag�ӱ���ͼ����ͼ\n" +
                            "option: \n" +
                            "   -h  ����\n" +
                            "example: \n" +
                            "   \"��ɬͼ ���L����\""
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
                        subject.sendMessage("��....�ƺ�û����")
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
                    subject.sendMessage("����ɾ��: $id")

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
                        "${id}��ɾ��\n" +
                            "ɾ��${imgNum}��ͼƬ    ${tags.size + 1}����¼"
                    )
                }
            }
        }
    }
}