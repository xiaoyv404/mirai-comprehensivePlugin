package com.xiaoyv404.mirai

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.yamlkt.Comment

object PluginConfig : AutoSavePluginConfig("404.404ComprehensiveBotConfig") {
    val redis by value<RedisConfig>()
    val postgres by value<PostgresConfig>()
    val etc by value<EtcConfig>()


    @Serializable
    data class PostgresConfig(
        @Comment(
            """
        数据库地址，支持postgres数据库.
        默认值：127.0.0.1
    """
        )
        val address: String = "127.0.0.1",
        @Comment(
            """
        数据库登入用户.
        默认值：postgres
    """
        )
        val user: String = "postgres",
        @Comment("数据库登入密码")
        val password: String = "",
        @Comment("数据库表单，表示要将数据存储在这个表单里")
        var table: String = "404",
        @Comment("SQL连接的附加参数，记得以&开头")
        val AdditionalParameters: String = "",
        @Comment("最大连接数，也许是连接池的大小？？？")
        var maximumPoolSize: Int? = 10,


        )

    @Serializable
    data class RedisConfig(
        @Comment("Ip")
        val Address: String = "127.0.0.1",
        @Comment("password")
        val Password: String = ""
    )

    @Serializable
    data class EtcConfig(
        @Comment("SauceNAOAPIKEY")
        val sauceNaoApiKey: String = "",
        @Comment("planApiUrl")
        val planApiUrl: String = "",
        @Comment("mcssApiUrl")
        val mcssApiUrl: String = ""
    )
}