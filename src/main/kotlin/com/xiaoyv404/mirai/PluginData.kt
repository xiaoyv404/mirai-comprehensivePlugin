package com.xiaoyv404.mirai

import kotlinx.serialization.*
import net.mamoe.mirai.console.data.*

object PluginData : AbstractPluginData() {
    override val saveName: String = "404DataBase"

    @ValueDescription("事件记录")
    val eventMap by value<MutableMap<Long, NfNewFriendRequestEvent>>()

    @ValueDescription("deBug")
    var deBug by value<Boolean>(false)

    @Serializable
    data class NfNewFriendRequestEvent(
        @SerialName("eventId")
        val eventId: Long = 0,
        @SerialName("message")
        val message: String = "",
        @SerialName("fromId")
        val fromId: Long = 0,
        @SerialName("fromGroupId")
        val fromGroupId: Long = 0,
        @SerialName("fromNick")
        val fromNick: String = ""
    )
}