package com.xiaoyv404.mirai.service.setu

import com.xiaoyv404.mirai.PluginConfig
import com.xiaoyv404.mirai.service.getUserInformation
import com.xiaoyv404.mirai.service.helper.Setu
import com.xiaoyv404.mirai.service.tool.downloadImage
import io.ktor.util.*
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.File

const val setuAPIurl = "https://api.nmb.show/1985acg.php"

@KtorExperimentalAPI
fun setuEntrance() {
    GlobalEventChannel.subscribeGroupMessages {
        finding(regular.setu) {
            var num = regular.setu.find(message.contentToString())!!.value.toInt()
            when (num) {
                0      -> group.sendMessage("w?你到底想让404干什么呢, 喵")
                9      -> group.sendMessage("9?是这个⑨吗?www")
                114514 -> group.sendMessage("好臭啊啊啊啊")
            }
            if (num > 5 && getUserInformation(sender.id).setu != true) {
                num = if (9 == (5..10).random()) {
                    group.sendMessage("去死啊你这个变态, 要看自己去Pixiv看")
                    0
                } else {
                    5
                }
            }
            if (num != 0)
                group.sendMessage("少女祈祷ing")

            for (i in 1..num) {
                val im = downloadImage(setuAPIurl)
                if (im != null)
                    subject.sendImage(im)
                else
                    group.sendMessage("`(*>﹏<*)′服务器酱好像不理我惹")
            }
        }
        startsWith("添加涩图") {
            if (message.contentToString().contains("-h"))
                group.sendMessage(Setu.add)
            else {
                try {
                    val id = message.contentToString()
                        .replace("添加涩图", "")
                        .replace(" ", "").toLong()

                    val ii = unformat(id, sender.id)
                    when (ii.picturesMun) {
                        0    -> group.sendMessage("出错啦(详见控制台)")
                        1    -> group.sendMessage(
                            File("${PluginConfig.database.SaveAddress}${ii.id}.png")
                                .uploadAsImage(group, "png").plus(sequenceInformation(ii))
                        )
                        else -> {
                            group.sendMessage(sequenceInformation(ii))
                            for (i in 1..ii.picturesMun)
                                group.sendImage(File("${PluginConfig.database.SaveAddress}${ii.id}-$i.png"))
                        }
                    }

                } catch (e: NumberFormatException) {
                    group.sendMessage(
                        "输入值错误\n" +
                            "请输入\"添加涩图 -h\"以查看用法"
                    )
                } catch (e: Exception) {
                    println(e)
                    group.sendMessage("出错啦(详见控制台)")
                }
            }
        }
        startsWith("搜涩图") {
            if (message.contentToString().contains("-h"))
                group.sendMessage(Setu.search)
            else {
                val tags = message.contentToString()
                    .replace("搜涩图", "")
                    .replace(" ", "")
                if (tags == "")
                    group.sendMessage("输入值错误\n" + "请输入\"搜涩图 -h\"以查看用法")
                else {
                    val ii = queryByTag(tags)
                    when (ii.picturesMun) {
                        0    -> group.sendMessage("唔....似乎没有呢")
                        1    -> group.sendMessage(
                            File("${PluginConfig.database.SaveAddress}${ii.id}.png")
                                .uploadAsImage(group, "png").plus(sequenceInformation(ii))
                        )
                        else -> {
                            group.sendMessage(sequenceInformation(ii))
                            for (i in 1..ii.picturesMun)
                                group.sendImage(File("${PluginConfig.database.SaveAddress}${ii.id}-$i.png"))
                        }
                    }
                }
            }
        }
    }
}