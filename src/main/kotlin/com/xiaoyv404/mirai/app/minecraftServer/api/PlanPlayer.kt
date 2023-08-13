package com.xiaoyv404.mirai.app.minecraftServer.api

data class PlanPlayer(
    val BASE_USER: BASEUSER,
    val banned: Boolean,
    val death_count: Int,
    val geo_info: List<GeoInfo>,
    val kick_count: Int,
    val lastSeen: Long,
    val mob_kill_count: Int,
    val name: String,
    val `operator`: Boolean,
    val player_kill_count: Int,
    val registered: Long,
    val uuid: String
)

data class BASEUSER(
    val name: String,
    val registered: Long,
    val timesKicked: Int,
    val uuid: String
)

data class GeoInfo(
    val date: Long,
    val geolocation: String
)