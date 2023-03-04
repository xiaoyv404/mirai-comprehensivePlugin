@file:Suppress("PropertyName")

package com.xiaoyv404.mirai.app.test

data class ServerOverview(
    val last_7_days: Last7Days,
    val numbers: Numbers,
    val timestamp: Long,
    val timestamp_f: String,
    val weeks: Weeks
)

data class Last7Days(
    val average_tps: String,
    val downtime: String,
    val low_tps_spikes: Int,
    val new_players: Int,
    val new_players_retention: Int,
    val new_players_retention_perc: String,
    val unique_players: Int,
    val unique_players_day: Int
)

data class Numbers(
    val best_peak_date: String,
    val best_peak_players: String,
    val current_uptime: String,
    val deaths: Int,
    val last_peak_date: String,
    val last_peak_players: String,
    val mob_kills: Int,
    val online_players: Int,
    val player_kills: Int,
    val player_playtime: String,
    val playtime: String,
    val regular_players: Int,
    val sessions: Int,
    val total_players: Int
)

data class Weeks(
    val average_playtime_after: String,
    val average_playtime_before: String,
    val average_playtime_trend: AveragePlaytimeTrend,
    val deaths_after: Int,
    val deaths_before: Int,
    val deaths_trend: DeathsTrend,
    val end: String,
    val midpoint: String,
    val mob_kills_after: Int,
    val mob_kills_before: Int,
    val mob_kills_trend: MobKillsTrend,
    val new_after: Int,
    val new_before: Int,
    val new_trend: NewTrend,
    val player_kills_after: Int,
    val player_kills_before: Int,
    val player_kills_trend: PlayerKillsTrend,
    val regular_after: Int,
    val regular_before: Int,
    val regular_trend: RegularTrend,
    val sessions_after: Int,
    val sessions_before: Int,
    val sessions_trend: SessionsTrend,
    val start: String,
    val unique_after: Int,
    val unique_before: Int,
    val unique_trend: UniqueTrend
)

data class AveragePlaytimeTrend(
    val direction: String,
    val reversed: Boolean,
    val text: String
)

data class DeathsTrend(
    val direction: String,
    val reversed: Boolean,
    val text: String
)

data class MobKillsTrend(
    val direction: String,
    val reversed: Boolean,
    val text: String
)

data class NewTrend(
    val direction: String,
    val reversed: Boolean,
    val text: String
)

data class PlayerKillsTrend(
    val direction: String,
    val reversed: Boolean,
    val text: String
)

data class RegularTrend(
    val direction: String,
    val reversed: Boolean,
    val text: String
)

data class SessionsTrend(
    val direction: String,
    val reversed: Boolean,
    val text: String
)

data class UniqueTrend(
    val direction: String,
    val reversed: Boolean,
    val text: String
)