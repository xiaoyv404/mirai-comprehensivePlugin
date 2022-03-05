package com.xiaoyv404.mirai.databace

import com.xiaoyv404.mirai.PluginConfig
import com.xiaoyv404.mirai.PluginMain
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.warning
import org.ktorm.database.Database
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel

object Database {
    sealed class ConnectionStatus {
        object CONNECTED : ConnectionStatus()
        object DISCONNECTED : ConnectionStatus()
    }

lateinit var db: Database
    private var connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED

    fun connect() {
        try {
            db = Database.connect(
                hikariDataSourceProvider(),
                logger = ConsoleLogger(threshold = LogLevel.INFO)
            )
//            rdb = genericObjectPoolSourceProvider()
            connectionStatus = ConnectionStatus.CONNECTED
            PluginMain.logger.info { "Database ${PluginConfig.database.table} is connected." }
        } catch (ex: Exception) {
            when (ex) {
                //当配置文件的配置不符合要求时throw
                is InvalidDatabaseConfigException -> {
                    throw ex
                }
            }
        }
    }

    @OptIn(ConsoleExperimentalApi::class)
    private fun hikariDataSourceProvider(): HikariDataSource = HikariDataSource(HikariConfig().apply {
        when {

            PluginConfig.database.address == ""           -> throw InvalidDatabaseConfigException("Database address is not set in config file ${PluginConfig.saveName}.")
            PluginConfig.database.table == ""             -> {
                PluginMain.logger.warning { "Database table is not set in config file ${PluginConfig.saveName} and now it will be default value 'sctimetabledb'." }
                PluginConfig.database.table = "mirai-404"
            }
            PluginConfig.database.user == ""              -> throw InvalidDatabaseConfigException("Database user is not set in config file ${PluginConfig.saveName}.")
            PluginConfig.database.password == ""          -> throw InvalidDatabaseConfigException("Database password is not set in config file ${PluginConfig.saveName}.")
            PluginConfig.database.maximumPoolSize == null -> {
                PluginMain.logger.warning { "Database maximumPoolSize is not set in config file ${PluginConfig.saveName} and now it will be default value 10." }
                PluginConfig.database.maximumPoolSize = 10
            }
        }
        jdbcUrl =
            "jdbc:mysql://${PluginConfig.database.address}/${PluginConfig.database.table}${PluginConfig.database.AdditionalParameters}"
        driverClassName = "com.mysql.cj.jdbc.Driver"
        username = PluginConfig.database.user
        password = PluginConfig.database.password
        maximumPoolSize = PluginConfig.database.maximumPoolSize!!
    })
}

