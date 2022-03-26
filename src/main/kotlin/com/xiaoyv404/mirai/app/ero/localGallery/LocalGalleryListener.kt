package com.xiaoyv404.mirai.app.ero.localGallery

import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.databace.dao.authorityIdentification
import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor
import com.xiaoyv404.mirai.core.NfApp
import com.xiaoyv404.mirai.databace.dao.gallery.*
import com.xiaoyv404.mirai.databace.dao.isAdmin
import com.xiaoyv404.mirai.databace.dao.isNotBot
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.nextMessage
import org.apache.commons.cli.Options

@App
class LocalGallery : NfApp(), IFshApp {
    override fun getAppName() = "LocalGallery"
    override fun getVersion() = "1.0.0"
    override fun getAppDescription() = "本地图库"
    override fun getCommands() = arrayOf("-ero")

    private val options = Options().apply {
        addOption("n", "no-outPut", false, "关闭输出")
    }

    private val log = PluginMain.logger

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        val cmdLine = IFshApp.cmdLine(options, args)

        if (args[1] == "add") {
            eroAdd(args.getOrNull(2), msg, cmdLine.hasOption("no-outPut"))
            return true
        }

        if (args[1] == "search") {
            val tagName = args.getOrNull(2)
            if (tagName == null) {
                MessageProcessor.reply(msg, "没名字我怎么搜嘛")
                return true
            }
            eroSearch(tagName, msg)
        }

        if (args[1] == "remove"){
            val id = args.getOrNull(2)?.toLongOrNull()
            if (id == null){
                MessageProcessor.reply(msg, "没ID我怎么删嘛")
                return true
            }
            eroRemove(id,msg)
        }

        return true
    }

    /**
     * 向本地图库添加图片
     * @author xiaoyv_404
     * @create 2022/3/19
     *
     * @param idData
     * @param msg
     * @param noOutPut
     */
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
                    subject.sendMessage("没找到图片ID捏，请发送图片ID")
                    msg.nextMessage().contentToString()
                } else
                    idData
            ).toList()

            log.info("找到${ids.size}个ID")

            ids.forEachIndexed { index, id ->
                log.info("下载编号 ${ids.size - 1}\\$index id ${id.value}")
                if (LocalGallerys(subject).unformat(id.value, sender.id, noOutPut)) {
                    log.info("下载编号 $index id ${id.value} 失败")
                    fail.add(id.value)
                }
            }

            if (fail.isNotEmpty()) {
                subject.sendMessage("下载失败 Id 列表")
                subject.sendMessage(fail.joinToString("，"))
            }
            if (ids.size >= 5) {
                subject.sendMessage("完成啦w!")
            }
        }
    }

    /**
     * 通过 tagName 搜索图片并随机发送
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
            log.info("[LocalGallerySearch] 尝试从本地图库搜索 Tag 包含 $tagNameA 的图片")
            val tagidA = GalleryTag {
                tagname = tagNameA
            }.findTagIdByTagName()
            if (tagidA == null) {
                log.info("[LocalGallerySearch] 未搜索到 TagName $tagNameA")
                subject.sendMessage("唔....似乎没有呢")
                return
            }

            log.info("[LocalGallerySearch] 搜索到 TagName $tagNameA ID $tagidA")

            val idAL = GalleryTagMap {
                tagid = tagidA
            }.findPidByTagId()

            log.info("[LocalGallerySearch] 搜索到 ID $tagidA 数量 ${idAL.size}")

            val idA = idAL.random()

            log.info("[LocalGallerySearch] 随机到 Pid $idA")

            val ii = Gallery {
                id = idA
            }.findById()
            LocalGallerys(subject).send(ii!!)
        }
    }


    /**
     * 通过 pid 删除本地图库图片
     * @author xiaoyv_404
     * @create 2022/3/19
     *
     * @param idA
     * @param msg
     */
    private suspend fun eroRemove(idA: Long, msg: MessageEvent) {
        val subject = msg.subject
        val sender = msg.sender
        if (sender.isAdmin()) {
            subject.sendMessage("正在删除: $idA")

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
                "${idA}已删除\n" +
                    "删除${imgNum}张图片    ${tags.size + 1}条记录"
            )
        }
    }
}