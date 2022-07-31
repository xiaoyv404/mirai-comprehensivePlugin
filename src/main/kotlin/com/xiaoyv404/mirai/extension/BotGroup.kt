package com.xiaoyv404.mirai.extension

import net.mamoe.mirai.*
import net.mamoe.mirai.contact.*

var bot: Bot? = null

/**
 * 通过[this]gid 获取群
 * @author xiaoyv_404
 *
 * @return
 */
fun Long.getGroup(): Group? {
    if (bot == null)
        bot = Bot.getInstance(2079373402)
    return bot!!.getGroup(this)
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