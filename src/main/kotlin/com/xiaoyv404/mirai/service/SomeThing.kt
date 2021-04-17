package com.xiaoyv404.mirai.service

import io.ktor.util.*
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.data.buildForwardMessage


@KtorExperimentalAPI
fun someThinkEntrance() {
    GlobalEventChannel.subscribeGroupMessages {
        always {
            if (getUserInformation(sender.id).bot != true) {
                val entryMassage = queryTerm(message.serializeToMiraiCode())
                if (entryMassage != "")
                    group.sendMessage(MiraiCode.deserializeMiraiCode(entryMassage))
            }
        }

        case("BiliBili��������") {
            group.sendMessage(
                "w, BiliBili��������������" +
                    if (groupDataRead(group.id) == true)
                        "��"
                    else
                        "��"
                            + "��Ŷ"
            )
        }

        case("����BiliBili��������") {
            val biliStatus: Boolean? = groupDataRead(group.id)
            println(biliStatus)
            if (biliStatus != null)
                groupDataUpdate(group.id, !biliStatus)
            else
                groupDataCreate(group.id)
            group.sendMessage(
                "���������~ BiliBili��������������" +
                    if (biliStatus != true)
                        "��"
                    else
                        "��"
                            + "��Ŷ"
            )
        }

        containsAny("��ƬƬ").invoke {
            group.sendMessage(
                message.serializeToMiraiCode()
                    .replace("��ƬƬ", "")
            )
        }

        startsWith("��ӻ�����") {
            val id = message.contentToString()
                .replace("��ӻ�����", "")
                .replace("@", "")
                .replace(" ", "")
            if (id == "")
                group.sendMessage("����ֵ����\n" + "������\"��ӻ����� -h\"�Բ鿴�÷�")
            else {
                val idL = id.toLong()
                val status: Boolean? = getUserInformation(idL).bot
                if (status != null) {
                    updateUserSetu(idL, !status)
                    group.sendMessage("Done")
                } else {
                    createUserInformation(idL, true, setu = false)
                    group.sendMessage("Done")
                }
            }
        }
        case("test") {
            group.sendMessage(
                buildForwardMessage {

                    2083664136 named "С��" says "Chino����"
                }
            )
        }
    }
}