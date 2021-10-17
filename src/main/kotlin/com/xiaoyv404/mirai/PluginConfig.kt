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
        数据库地址，支持MySQL数据库.
        默认值：localhost
    """
    )
    val address: String = "localhost",
    @Comment(
        """
        数据库登入用户.
        默认值：root
    """
    )
    val user: String = "root",
    @Comment("数据库登入密码")
    val password: String = "",
    @Comment("数据库表单，表示要将数据存储在这个表单里")
    var table: String = "",
    @Comment("SQL连接的附加参数，记得以&开头")
    val AdditionalParameters: String = "",
    @Comment("最大连接数，也许是连接池的大小？？？")
    var maximumPoolSize: Int? = 10,
    @Comment("本地代理端口")
    val ProxyPort: Int = 404,
    @Comment("SauceNAOAPIKEY")
    val sauceNaoApiKey: String = ""
)