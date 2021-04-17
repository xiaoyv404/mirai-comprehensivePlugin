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

        case("BiliBili解析功能") {
            group.sendMessage(
                "w, BiliBili解析功能现在是" +
                    if (groupDataRead(group.id) == true)
                        "开"
                    else
                        "关"
                            + "的哦"
            )
        }

        case("开关BiliBili解析功能") {
            val biliStatus: Boolean? = groupDataRead(group.id)
            println(biliStatus)
            if (biliStatus != null)
                groupDataUpdate(group.id, !biliStatus)
            else
                groupDataCreate(group.id)
            group.sendMessage(
                "任务完成辣~ BiliBili解析功能现在是" +
                    if (biliStatus != true)
                        "开"
                    else
                        "关"
                            + "的哦"
            )
        }

        containsAny("切片片").invoke {
            group.sendMessage(
                message.serializeToMiraiCode()
                    .replace("切片片", "")
            )
        }

        startsWith("添加机器人") {
            val id = message.contentToString()
                .replace("添加机器人", "")
                .replace("@", "")
                .replace(" ", "")
            if (id == "")
                group.sendMessage("输入值错误\n" + "请输入\"添加机器人 -h\"以查看用法")
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

                    2083664136 named "小宇" says "Chino赛高"
                }
            )
        }
    }
}