package com.xiaoyv404.mirai.extension

import net.mamoe.mirai.contact.*

/**
 * 通过[this]gid 获取群
 * @author xiaoyv_404
 *
 * @return
 */
fun Long.getFriend(): Friend? {
    return bot.getFriend(this)
}