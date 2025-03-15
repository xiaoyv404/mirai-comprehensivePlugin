package com.xiaoyv404.mirai.app

import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfAppMessageHandler
import com.xiaoyv404.mirai.core.uid
import com.xiaoyv404.mirai.dao.*
import com.xiaoyv404.mirai.model.*
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import java.time.LocalDateTime

@App
class UserAlert : NfAppMessageHandler(), IFshApp {
    override fun getAppName() = "UserAlert"
    override fun getVersion() = "1.0.1"
    override fun getAppDescription() = "用户警告相关"
    override fun getCommands() = arrayOf("-警告查询", "-alerttop", "-警告次数")
    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        if (msg.groupType() != GroupType.MCG) return false
        val sender = msg.sender as Member
        if (msg.isNotAdmin() && !sender.permission.isOperator()) return false
        return when (args[0]) {
            "-警告查询" -> {
                val id = args.getOrNull(1)?.toLongOrNull()
                if (id == null) {
                    msg.reply("参数错误", true)
                    return false
                }
                alertGet(id, msg)
            }

            "-alerttop" -> {
                alertTop(msg)
            }

            "-警告次数" -> {
                val items = args.getOrNull(1)?.toIntOrNull()
                if (items == null) {
                    msg.reply("请输入次数", true)
                    return false
                }
                alertGetByTimes(items, msg)
            }

            else -> false
        }
    }

    private suspend fun alertTop(msg: MessageEvent): Boolean {
        val reply = Users.getAll()
            .filter { it.warningTimes > 0 }
            .sortedByDescending { it.warningTimes }
            .joinToString("\n") { "${it.id}共收到${it.warningTimes}次警告" }
        msg.reply(reply)
        return true
    }

    private suspend fun alertGet(id: Long, msg: MessageEvent): Boolean {
        val user = User {
            this.id = id
        }.findById()
        if (user == null) {
            msg.reply("无记录")
            return false
        }
        val logs = UserAlertLog {
            this.target = id
        }.findByID()

        val reply = buildMessageChain {
            +"${user.id}共收到${user.warningTimes}次警告"
            logs.forEach {
                +"\n${it.executor} ${it.type.name} ${it.time} ${it.reason}"
            }
        }
        println(reply)

        msg.reply(reply)
        return true
    }

    private suspend fun alertGetByTimes(times: Int, msg: MessageEvent): Boolean {
        Users.getByWarningTimes(times)
            .joinToString("\n") { "${it.id}" }
            .let {
                if (it == "")
                    msg.reply("盯~没有人诶")
                else
                    msg.reply(it)
            }
        return true
    }

    override suspend fun handleMessage(msg: MessageEvent) {
        if (!msg.message.contentToString().startsWith("警告")) return
        if (msg.groupType() != GroupType.MCG) return
        val sender = msg.sender as Member
        if (msg.isNotAdmin() && !sender.permission.isOperator()) return

        val ats = msg.message.filterIsInstance<At>()
        if (ats.isEmpty()) return

        val reason = msg.message.filterIsInstance<PlainText>().last().content
        val str = MessageChainBuilder()
        ats.forEachIndexed { k, v ->
            UserAlertLog {
                this.target = v.target
                this.executor = msg.uid()
                this.time = LocalDateTime.now()
                this.type = UserAlertType.Increase
                this.reason = reason
            }.add()
            val user = User {
                this.id = v.target
            }.findById() ?: User {
                this.id = v.target
            }
            user.warningTimes++

            user.save()
            str.append(buildMessageChain {
                +"已警告"
                +v
                +"，本次为第${user.warningTimes}次警告"
                +"\n"
            })
        }
        str.append(PlainText("原因为：${if (reason!="") reason else "无原因"}"))

        msg.reply(str.build())

        return
    }
}
