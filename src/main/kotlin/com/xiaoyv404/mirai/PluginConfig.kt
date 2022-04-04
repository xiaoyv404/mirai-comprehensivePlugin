package com.xiaoyv404.mirai

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.yamlkt.Comment

object PluginConfig : AutoSavePluginConfig("404.404ComprehensiveBotConfig") {
    val database by value<DatabaseConfig>()
}

@Serializable
data class DatabaseConfig(
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
    @Comment("Redis Ip")
    val redisAddress: String = "127.0.0.1",
    @Comment("Redis password")
    val redisPassword: String = "",
    @Comment("本地代理端口")
    val ProxyPort: Int = 7890,
    @Comment("SauceNAOAPIKEY")
    val sauceNaoApiKey: String = ""
)