package com.xiaoyv404.mirai.app.groupHelper

import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.App
import com.xiaoyv404.mirai.core.MessageProcessor.reply
import com.xiaoyv404.mirai.core.NfApp
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.file.AbsoluteFileFolder.Companion.extension
import net.mamoe.mirai.contact.file.AbsoluteFileFolder.Companion.nameWithoutExtension
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.event.events.MessageEvent
import java.util.*

@App
class GroupFile : NfApp(), IFshApp {
    override fun getAppName() = "群文件管理助手"
    override fun getVersion() = "1.0.2"
    override fun getAppDescription() = "帮助整理群文件"
    override fun getCommands() = arrayOf("-file")

    override suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean {
        if (args.size < 2)
            return false
        when (args[1]) {
            "pack" -> pack(msg)
        }
        return true
    }

    //todo 添加至配置文件
    private val extensionMap = mapOf(
        "png" to "图片",
        "pdf" to "文档",
        "jpg" to "图片",
        "mp4" to "视频",
        "mp3" to "音频",
        "wav" to "音频",
        "mkv" to "视频",
        "zip" to "压缩包",
        "7z" to "压缩包",
        "mov" to "视频",
        "rar" to "压缩包",
        "exe" to "应用程序",
        "py" to "code",
        "jar" to "应用程序",
        "apk" to "应用程序",
        "jpeg" to "图片",
        "gif" to "图片",
        "m4a" to "音频",
        "doc" to "文档",
        "docx" to "文档",
        "flac" to "音频",
        "aac" to "音频",
        "torrent" to "种子",
        "bat" to "脚本",
        "sh" to "脚本",
        "nbt" to "数据",
        "litematic" to "投影文件",
        "html" to "code",
        "kt" to "code",
        "java" to "code",
        "go" to "code",
        "js" to "脚本",
        "css" to "code",
        "md" to "文档",
        "json" to "数据",
        "nbt" to "数据",
        "db" to "数据",
        "litemod" to "数据",
        "crx" to "数据",
        "webp" to "图片",
        "flv" to "视频",
        "bmp" to "图片",
        "avi" to "视频",
        "txt" to "数据"
    )

    private suspend fun pack(msg: MessageEvent) {
        val subject = msg.subject
        if (subject !is Group)
            return
        if (!subject.botAsMember.permission.isOperator()) {
            msg.reply("呜呜呜，404没有权限")
            return
        }
        val changeQuantities = mutableMapOf<String, Long>()
        val root = subject.files.root
        var mark = true
        //循环执行至完成
        while (mark) {
            mark = false
            root.files().collect {
                val name = it.nameWithoutExtension
                val extension = it.extension.lowercase(Locale.getDefault())
                val folderName = extensionMap[extension] ?: return@collect //todo: 如果此文件没匹配到，发送消息至主人
                log.info("文件名: $name  拓展名: $extension  文件夹: $folderName")
                val folder = root.createFolder(folderName)
                it.moveTo(folder)
                val changeQuantity = changeQuantities.getOrDefault(folderName, 0)
                changeQuantities[folderName] = changeQuantity + 1
                log.info("已将$name 移动至$folderName")
                mark = true
            }
        }
        //格式化整理文件
        val str = StringBuilder().run {
            changeQuantities.forEach { (n, q) ->
                append("$n: $q\n")
            }
            toString()
        }

        msg.reply(
            """
整理完成，已整理文件统计: 
$str
        """.trimIndent()
        )
    }
}