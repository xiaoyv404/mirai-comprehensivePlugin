package com.xiaoyv404.mirai.app.ero.localGallery

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.core.gid
import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.databace.dao.authorityIdentification
import com.xiaoyv404.mirai.databace.dao.gallery.*
import com.xiaoyv404.mirai.databace.dao.isNotAdmin
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.nextMessage
import org.apache.commons.cli.Options

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

        when (args[1]) {
            "add"    -> eroAdd(args.getOrNull(2), msg, cmdLine.hasOption("no-outPut"))
            "search" -> {
                val tagName = args.getOrNull(2)
                if (tagName == null) {
                    msg.reply("û��������ô����")
                    return true
                }
                eroSearch(tagName, msg)
            }
            "remove" -> {
                val id = args.getOrNull(2)?.toLongOrNull()
                if (id == null) {
                    msg.reply("ûID����ôɾ��")
                    return true
                }
                eroRemove(id, msg)
            }

        }

        return true
    }

    /**
     * �򱾵�ͼ�����ͼƬ
     * @author xiaoyv_404
     * @create 2022/3/19
     *
     * @param idData
     * @param msg
     * @param noOutPut
     */
    private suspend fun eroAdd(idData: String?, msg: MessageEvent, noOutPut: Boolean = false) {
        if (authorityIdentification(msg.uid(), msg.gid(), "LocalGallery")) {
            val fail = mutableListOf<String>()
            val ids = Regex("\\d+").findAll(
                if (idData == null) {
                    msg.reply("û�ҵ�ͼƬID���뷢��ͼƬID", true)
                    msg.nextMessage().contentToString()
                } else
                    idData
            ).toList()

            log.info("�ҵ�${ids.size}��ID")

            ids.forEachIndexed { index, id ->
                log.info("���ر�� ${ids.size - 1}\\$index id ${id.value}")
                if (LocalGallerys(msg.subject).unformat(id.value, msg.uid(), noOutPut)) {
                    log.info("���ر�� $index id ${id.value} ʧ��")
                    fail.add(id.value)
                }
            }

            if (fail.isNotEmpty()) {
                msg.reply("����ʧ�� Id �б�")
                msg.reply(fail.joinToString("��"))
            }
            if (ids.size >= 5) {
                msg.reply("�����w!")
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
        if (authorityIdentification(msg.uid(), msg.gid(), "LocalGallery")) {
            log.info("[LocalGallerySearch] ���Դӱ���ͼ������ Tag ���� $tagNameA ��ͼƬ")
            val tagidA = GalleryTag {
                tagname = tagNameA
            }.findTagIdByTagName()
            if (tagidA == null) {
                log.info("[LocalGallerySearch] δ������ TagName $tagNameA")
                msg.reply("��....�ƺ�û����",true)
                return
            }

            log.info("[LocalGallerySearch] ������ TagName $tagNameA ID $tagidA")

            val idAL = GalleryTagMap {
                tagid = tagidA
            }.findPidByTagId()

            log.info("[LocalGallerySearch] ������ ID $tagidA ���� ${idAL.size}")

            val idA = idAL.random()

            log.info("[LocalGallerySearch] ����� Pid $idA")

            val ii = Gallery {
                id = idA
            }.findById()
            LocalGallerys(msg.subject).send(ii!!)
        }
    }


    /**
     * ͨ�� pid ɾ������ͼ��ͼƬ
     * @author xiaoyv_404
     * @create 2022/3/19
     *
     * @param idA
     * @param msg
     */
    private suspend fun eroRemove(idA: Long, msg: MessageEvent) {
        if (msg.isNotAdmin())
            return
        msg.reply("����ɾ��: $idA")

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
        msg.reply(
            "${idA}��ɾ��\n" +
                "ɾ��${imgNum}��ͼƬ    ${tags.size + 1}����¼"
        )
    }
}