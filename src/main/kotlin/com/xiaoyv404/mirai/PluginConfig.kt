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
        ���ݿ��ַ��֧��postgres���ݿ�.
        Ĭ��ֵ��127.0.0.1
    """
    )
    val address: String = "127.0.0.1",
    @Comment(
        """
        ���ݿ�����û�.
        Ĭ��ֵ��postgres
    """
    )
    val user: String = "postgres",
    @Comment("���ݿ��������")
    val password: String = "",
    @Comment("���ݿ������ʾҪ�����ݴ洢���������")
    var table: String = "404",
    @Comment("SQL���ӵĸ��Ӳ������ǵ���&��ͷ")
    val AdditionalParameters: String = "",
    @Comment("�����������Ҳ�������ӳصĴ�С������")
    var maximumPoolSize: Int? = 10,
    @Comment("Redis Ip")
    val redisAddress: String = "127.0.0.1",
    @Comment("Redis password")
    val redisPassword: String = "",
    @Comment("���ش���˿�")
    val ProxyPort: Int = 7890,
    @Comment("SauceNAOAPIKEY")
    val sauceNaoApiKey: String = ""
)