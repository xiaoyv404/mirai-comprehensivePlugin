package com.xiaoyv404.mirai.service.ero.localGallery

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.databace.dao.gallery.GalleryTag
import com.xiaoyv404.mirai.databace.dao.gallery.update
import com.xiaoyv404.mirai.databace.dao.itAdmin
import com.xiaoyv404.mirai.databace.dao.itNotBot
import com.xiaoyv404.mirai.service.accessControl.authorityIdentification
import com.xiaoyv404.mirai.service.ero.*
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
                )) && sender.itNotBot()
            ) {
                var num = it.groups[3]!!.value.toInt()
                when (num) {
                    0      -> subject.sendMessage("w?�㵽������404��ʲô��, ��")
                    9      -> subject.sendMessage("9?���������?www")
                    114514 -> subject.sendMessage("�ó���������")
                }
                if (num > 5 && sender.itNotBot()) {
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
            if (authorityIdentification(
                    sender.id,
                    subject.id,
                    "LocalGallery"
                ) && sender.itNotBot()
            ) {
                val fail = mutableListOf<String>()
                val rd = it.groups
                val ids = Regex("\\d+").findAll(
                    if (rd[3] == null) {
                        subject.sendMessage("û�ҵ�ͼƬID���뷢��ͼƬID")
                        nextMessage().contentToString()
                    } else
                        rd[3]!!.value
                ).toList()
                val noOutPut = rd[7]!=null
                PluginMain.logger.info("�ҵ�${ids.size}��ID")

                ids.forEachIndexed  { index, id ->
                    PluginMain.logger.info("���ر�� ${ids.size-1}\\$index id ${id.value}")
                    if (LocalGallery(subject).unformat(id.value, sender.id, noOutPut)) {
                        PluginMain.logger.info("���ر�� $index id ${id.value} ʧ��")
                        fail.add(id.value)
                    }
                }

                if (fail.isNotEmpty()){
                    subject.sendMessage("����ʧ�� Id �б�")
                    subject.sendMessage(fail.joinToString("��"))
                }
                if (ids.size >= 5){
                    subject.sendMessage("�����w!")
                }
            }
        }
        finding(Command.eroSearch) {
            if ((authorityIdentification(
                    sender.id,
                    subject.id,
                    "LocalGallery"
                )) && sender.itNotBot()
            ) {
                val rd = it.groups
                val tagid = queryTagIdByTag(rd[3]!!.value)
                if (tagid == null) {
                    subject.sendMessage("��....�ƺ�û����")
                    return@finding
                }

                val id = queryIdByTagId(tagid).random().toLong()
                val ii = getImgInformationById(id)
                LocalGallery(subject).send(ii)
            }
        }
        finding(Command.eroRemove) {
            if (sender.itAdmin()) {
                val rd = it.groups
                val id = rd[3]!!.value.toLong()
                subject.sendMessage("����ɾ��: $id")

                val tags = queryTagIdById(id)

                tags.forEach { tagidA ->
                    val numA = queryTagQuantityByTagId(tagidA)
                    GalleryTag {
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
                    "${id}��ɾ��\n" +
                        "ɾ��${imgNum}��ͼƬ    ${tags.size + 1}����¼"
                )
            }
        }
    }
}
