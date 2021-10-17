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
        ���ݿ��ַ��֧��MySQL���ݿ�.
        Ĭ��ֵ��localhost
    """
    )
    val address: String = "localhost",
    @Comment(
        """
        ���ݿ�����û�.
        Ĭ��ֵ��root
    """
    )
    val user: String = "root",
    @Comment("���ݿ��������")
    val password: String = "",
    @Comment("���ݿ������ʾҪ�����ݴ洢���������")
    var table: String = "",
    @Comment("SQL���ӵĸ��Ӳ������ǵ���&��ͷ")
    val AdditionalParameters: String = "",
    @Comment("�����������Ҳ�������ӳصĴ�С������")
    var maximumPoolSize: Int? = 10,
    @Comment("���ش���˿�")
    val ProxyPort: Int = 404,
    @Comment("SauceNAOAPIKEY")
    val sauceNaoApiKey: String = ""
)