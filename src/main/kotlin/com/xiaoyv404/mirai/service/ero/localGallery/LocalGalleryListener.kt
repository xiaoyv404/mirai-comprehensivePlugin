package com.xiaoyv404.mirai.service.ero.localGallery

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.databace.dao.gallery.*
import com.xiaoyv404.mirai.databace.dao.itAdmin
import com.xiaoyv404.mirai.databace.dao.itNotBot
import com.xiaoyv404.mirai.service.accessControl.authorityIdentification
import com.xiaoyv404.mirai.service.ero.setuAPIUrl
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
                val tagNameA = rd[3]!!.value
                PluginMain.logger.info("[LocalGallerySearch] ���Դӱ���ͼ������ Tag ���� $tagNameA ��ͼƬ")
                val tagidA = GalleryTag {
                    tagname = tagNameA
                }.findTagIdByTagName()
                if (tagidA == null) {
                    PluginMain.logger.info("[LocalGallerySearch] δ������ TagName $tagNameA")
                    subject.sendMessage("��....�ƺ�û����")
                    return@finding
                }

                PluginMain.logger.info("[LocalGallerySearch] ������ TagName $tagNameA ID $tagidA")

                val idAL = GalleryTagMap {
                    tagid = tagidA
                }.findPidByTagId()

                PluginMain.logger.info("[LocalGallerySearch] ������ ID $tagidA ���� ${idAL.size}")

                val idA = idAL.random()

                PluginMain.logger.info("[LocalGallerySearch] ����� Pid $idA")

                val ii = Gallery {
                    id = idA
                }.findById()
                LocalGallery(subject).send(ii!!)

            }
        }
        finding(Command.eroRemove) {
            if (sender.itAdmin()) {
                val rd = it.groups
                val idA = rd[3]!!.value.toLong()
                subject.sendMessage("����ɾ��: $idA")

                val tags = GalleryTagMap {
                    pid = idA
                }.findTagIdByPid()

                tags.forEach { tagidA ->
                    GalleryTag {
                        tagid = tagidA
                    }.reduceNumByTagId()
                }


                val information = Gallery {
                    id = idA
                }.findById()

                val imgNum = information!!.picturesMun
                val extension = information.extension

                if (imgNum == 1)
                    PluginMain.resolveDataFile("gallery/$idA.$extension").deleteRecursively()
                else
                    for (i in 1..imgNum) {
                        PluginMain.resolveDataFile("gallery/$idA-$i.$extension")
                            .deleteRecursively()
                    }
                GalleryTagMap {
                    pid = idA
                }.deleteByPid()

                Gallery {
                    id = idA
                }.deleteById()
                subject.sendMessage(
                    "${idA}��ɾ��\n" +
                        "ɾ��${imgNum}��ͼƬ    ${tags.size + 1}����¼"
                )
            }
        }
    }
}
