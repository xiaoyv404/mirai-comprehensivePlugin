package com.xiaoyv404.mirai.app.ero.localGallery

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.app.accessControl.authorityIdentification
import com.xiaoyv404.mirai.app.ero.setuAPIUrl
import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.databace.Command
import com.xiaoyv404.mirai.databace.dao.gallery.*
import com.xiaoyv404.mirai.databace.dao.isAdmin
import com.xiaoyv404.mirai.databace.dao.isNotBot
import com.xiaoyv404.mirai.tool.KtorUtils.normalClient
import io.ktor.client.request.*
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.nextMessage
import org.apache.commons.cli.Options
import java.io.InputStream

@App
class LocalGallery : NfApp(), IFshApp {
    override fun getAppName() = "LocalGallery"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "����ͼ��"
    override fun getCommands() = arrayOf("-ero")

    private val options = Options().apply {
        addOption("n", "no-outPut", false, "�ر����")
    }

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        val cmdLine = IFshApp.cmdLine(options, args)

        if (args[1] == "add") {
            eroAdd(args.getOrNull(2), msg, cmdLine.hasOption("no-outPut"))
            return true
        }

        if (args[1] == "search") {
            val tagName = args.getOrNull(2)
            if (tagName == null) {
                MessageProcessor.reply(msg, "û��������ô����")
                return true
            }
            eroSearch(tagName, msg)
        }

        return true
    }

    private suspend fun eroAdd(idData: String?, msg: MessageEvent, noOutPut: Boolean = false) {
        val subject = msg.subject
        val sender = msg.sender
        if (authorityIdentification(
                sender.id,
                subject.id,
                "LocalGallery"
            ) && sender.isNotBot()
        ) {
            val fail = mutableListOf<String>()
            val ids = Regex("\\d+").findAll(
                if (idData == null) {
                    subject.sendMessage("û�ҵ�ͼƬID���뷢��ͼƬID")
                    msg.nextMessage().contentToString()
                } else
                    idData
            ).toList()

            PluginMain.logger.info("�ҵ�${ids.size}��ID")

            ids.forEachIndexed { index, id ->
                PluginMain.logger.info("���ر�� ${ids.size - 1}\\$index id ${id.value}")
                if (LocalGallerys(subject).unformat(id.value, sender.id, noOutPut)) {
                    PluginMain.logger.info("���ر�� $index id ${id.value} ʧ��")
                    fail.add(id.value)
                }
            }

            if (fail.isNotEmpty()) {
                subject.sendMessage("����ʧ�� Id �б�")
                subject.sendMessage(fail.joinToString("��"))
            }
            if (ids.size >= 5) {
                subject.sendMessage("�����w!")
            }
        }
    }

    /**
     * ͨ�� tagName ����ͼƬ���������
     * @author xiaoyv_404
     * @create 2022/3/19
     *
     * @param tagNameA
     * @param msg
     */
    private suspend fun eroSearch(tagNameA: String, msg: MessageEvent) {
        val subject = msg.subject
        val sender = msg.sender
        if ((authorityIdentification(
                sender.id,
                subject.id,
                "LocalGallery"
            )) && sender.isNotBot()
        ) {
            PluginMain.logger.info("[LocalGallerySearch] ���Դӱ���ͼ������ Tag ���� $tagNameA ��ͼƬ")
            val tagidA = GalleryTag {
                tagname = tagNameA
            }.findTagIdByTagName()
            if (tagidA == null) {
                PluginMain.logger.info("[LocalGallerySearch] δ������ TagName $tagNameA")
                subject.sendMessage("��....�ƺ�û����")
                return
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
            LocalGallerys(subject).send(ii!!)
        }
    }
}

fun localGalleryListener() {
    GlobalEventChannel.subscribeMessages {
        finding(Command.ero) {
            if ((authorityIdentification(
                    sender.id,
                    subject.id,
                    "NetworkEro"
                )) && sender.isNotBot()
            ) {
                var num = it.groups[3]!!.value.toInt()
                when (num) {
                    0      -> subject.sendMessage("w?�㵽������404��ʲô��, ��")
                    9      -> subject.sendMessage("9?���������?www")
                    114514 -> subject.sendMessage("�ó���������")
                }
                if (num > 5 && sender.isNotBot()) {
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
        finding(Command.eroRemove) {
            if (sender.isAdmin()) {
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
