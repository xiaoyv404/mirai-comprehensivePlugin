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
                0      -> group.sendMessage("w?�㵽������404��ʲô��, ��")
                9      -> group.sendMessage("9?���������?www")
                114514 -> group.sendMessage("�ó���������")
            }
            if (num > 5 && getUserInformation(sender.id).setu != true) {
                num = if (9 == (5..10).random()) {
                    group.sendMessage("ȥ�����������̬, Ҫ���Լ�ȥPixiv��")
                    0
                } else {
                    5
                }
            }
            if (num != 0)
                group.sendMessage("��Ů��ing")

            for (i in 1..num) {
                val im = downloadImage(setuAPIurl)
                if (im != null)
                    subject.sendImage(im)
                else
                    group.sendMessage("`(*>�n<*)�������������������")
            }
        }
        startsWith("���ɬͼ") {
            if (message.contentToString().contains("-h"))
                group.sendMessage(Setu.add)
            else {
                try {
                    val id = message.contentToString()
                        .replace("���ɬͼ", "")
                        .replace(" ", "").toLong()

                    val ii = unformat(id, sender.id)
                    when (ii.picturesMun) {
                        0    -> group.sendMessage("������(�������̨)")
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
                        "����ֵ����\n" +
                            "������\"���ɬͼ -h\"�Բ鿴�÷�"
                    )
                } catch (e: Exception) {
                    println(e)
                    group.sendMessage("������(�������̨)")
                }
            }
        }
        startsWith("��ɬͼ") {
            if (message.contentToString().contains("-h"))
                group.sendMessage(Setu.search)
            else {
                val tags = message.contentToString()
                    .replace("��ɬͼ", "")
                    .replace(" ", "")
                if (tags == "")
                    group.sendMessage("����ֵ����\n" + "������\"��ɬͼ -h\"�Բ鿴�÷�")
                else {
                    val ii = queryByTag(tags)
                    when (ii.picturesMun) {
                        0    -> group.sendMessage("��....�ƺ�û����")
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