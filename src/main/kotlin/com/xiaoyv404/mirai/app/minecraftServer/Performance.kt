@file:Suppress("PropertyName")

package com.xiaoyv404.mirai.app.minecraftServer

data class Performance(
    val timestamp: Long,
    val timestamp_f: String,
//    val playersOnline:List<List<Number>>,
    val tps:List<List<Long>>,
//    val cpu:List<List<Number>>,
//    val ram:List<List<Number>>,
//    val entities:List<List<Number>>,
)
