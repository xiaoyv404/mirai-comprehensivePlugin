package com.xiaoyv404.mirai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AbstractPluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

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

object NfPluginData : AbstractPluginData() {
    override val saveName: String = "404DataBase"

    @ValueDescription("事件记录")
    val eventMap by value<MutableMap<Long, NfNewFriendRequestEvent>>()

    @ValueDescription("deBug")
    var deBug by value<Boolean>(false)
}