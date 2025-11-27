package com.xiaoyv404.mirai.app.minecraftServer.api

import com.google.gson.annotations.SerializedName

data class PlanPlayer(
    @SerializedName("banned")
    val banned: Boolean,

    @SerializedName("death_count")
    val deathCount: Int,

    @SerializedName("geo_info")
    val geoInfo: List<GeoInfo>,

    @SerializedName("kick_count")
    val kickCount: Int,

    @SerializedName("lastSeen")
    val lastSeen: Long,

    @SerializedName("mob_kill_count")
    val mobKillCount: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("operator")
    val op: Boolean,

    @SerializedName("player_kill_count")
    val playerKillCount: Int,

    @SerializedName("registered")
    val registered: Long,

    @SerializedName("uuid")
    val uuid: String
)

data class GeoInfo(
    val date: Long,
    val geolocation: String
)