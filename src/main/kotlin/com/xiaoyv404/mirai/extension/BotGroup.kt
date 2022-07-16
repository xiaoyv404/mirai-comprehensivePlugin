package com.xiaoyv404.mirai.extension

import com.xiaoyv404.mirai.*
import net.mamoe.mirai.contact.*

val bot = PluginMain.bot

/**
 * 通过[this]gid 获取群
 * @author xiaoyv_404
 *
 * @return
 */
fun Long.getGroup(): Group? {
    return bot.getGroup(this)
}

fun String.getGroup(): Group? {
    return this.toLong().getGroup()
}

/**
 * 通过[this]uid 和 [gid]gid 获取群成员
 *
 * @param gid
 * @return
 */
fun String.getMember(gid: String): Member? {
    return gid.getGroup()?.getMember(this.toLong())
}