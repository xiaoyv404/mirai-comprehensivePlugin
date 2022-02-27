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
                    0      -> subject.sendMessage("w?你到底想让404干什么呢, 喵")
                    9      -> subject.sendMessage("9?是这个⑨吗?www")
                    114514 -> subject.sendMessage("好臭啊啊啊啊")
                }
                if (num > 5 && sender.itNotBot()) {
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
                        subject.sendMessage("没找到图片ID捏，请发送图片ID")
                        nextMessage().contentToString()
                    } else
                        rd[3]!!.value
                ).toList()
                val noOutPut = rd[7]!=null
                PluginMain.logger.info("找到${ids.size}个ID")

                ids.forEachIndexed  { index, id ->
                    PluginMain.logger.info("下载编号 ${ids.size-1}\\$index id ${id.value}")
                    if (LocalGallery(subject).unformat(id.value, sender.id, noOutPut)) {
                        PluginMain.logger.info("下载编号 $index id ${id.value} 失败")
                        fail.add(id.value)
                    }
                }

                if (fail.isNotEmpty()){
                    subject.sendMessage("下载失败 Id 列表")
                    subject.sendMessage(fail.joinToString("，"))
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
                )) && sender.itNotBot()
            ) {
                val rd = it.groups
                val tagNameA = rd[3]!!.value
                PluginMain.logger.info("[LocalGallerySearch] 尝试从本地图库搜索 Tag 包含 $tagNameA 的图片")
                val tagidA = GalleryTag {
                    tagname = tagNameA
                }.findTagIdByTagName()
                if (tagidA == null) {
                    PluginMain.logger.info("[LocalGallerySearch] 未搜索到 TagName $tagNameA")
                    subject.sendMessage("唔....似乎没有呢")
                    return@finding
                }

                PluginMain.logger.info("[LocalGallerySearch] 搜索到 TagName $tagNameA ID $tagidA")

                val idAL = GalleryTagMap {
                    tagid = tagidA
                }.findPidByTagId()

                PluginMain.logger.info("[LocalGallerySearch] 搜索到 ID $tagidA 数量 ${idAL.size}")

                val idA = idAL.random()

                PluginMain.logger.info("[LocalGallerySearch] 随机到 Pid $idA")

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
}
