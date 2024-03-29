package com.xiaoyv404.mirai.database

import com.xiaoyv404.mirai.PluginConfig
import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.extension.MyPostgreSqlDialect
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.async.RedisAsyncCommands
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.warning
import org.ktorm.database.Database
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import java.time.Duration
import java.time.temporal.ChronoUnit

object Database {
    sealed class ConnectionStatus {
        object CONNECTED : ConnectionStatus()
        object DISCONNECTED : ConnectionStatus()
    }

    lateinit var db: Database
    lateinit var rdb: RedisAsyncCommands<String, String>
    private var connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED

    fun connect() {
        try {
            db = Database.connect(
                hikariDataSourceProvider(),
                logger = ConsoleLogger(threshold = LogLevel.WARN),
                dialect = MyPostgreSqlDialect()
            )
            rdb = lettuceDataSourceProvider().connect().async()
            connectionStatus = ConnectionStatus.CONNECTED
            PluginMain.logger.info { "Database ${PluginConfig.postgres.table} is connected." }
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
            PluginConfig.postgres.address == ""           -> throw InvalidDatabaseConfigException("Database address is not set in config file ${PluginConfig.saveName}.")
            PluginConfig.postgres.table == ""             -> {
                PluginMain.logger.warning { "Database table is not set in config file ${PluginConfig.saveName} and now it will be default value 'sctimetabledb'." }
                PluginConfig.postgres.table = "404"
            }
            PluginConfig.postgres.user == ""              -> throw InvalidDatabaseConfigException("Database user is not set in config file ${PluginConfig.saveName}.")
            PluginConfig.postgres.password == ""          -> throw InvalidDatabaseConfigException("Database password is not set in config file ${PluginConfig.saveName}.")
            PluginConfig.postgres.maximumPoolSize == null -> {
                PluginMain.logger.warning { "Database maximumPoolSize is not set in config file ${PluginConfig.saveName} and now it will be default value 10." }
                PluginConfig.postgres.maximumPoolSize = 10
            }
        }
        jdbcUrl =
            "jdbc:postgresql://${PluginConfig.postgres.address}/${PluginConfig.postgres.table}${PluginConfig.postgres.AdditionalParameters}"
        driverClassName = "org.postgresql.Driver"
        username = PluginConfig.postgres.user
        password = PluginConfig.postgres.password
        maximumPoolSize = PluginConfig.postgres.maximumPoolSize!!
    })

    private fun lettuceDataSourceProvider(): RedisClient = RedisClient.create(RedisURI.builder().apply {
        withHost(PluginConfig.redis.Address)
        withPort(6379)
        withTimeout(Duration.of(10, ChronoUnit.SECONDS))
        withPassword(PluginConfig.redis.Password.toCharArray())
    }.build())
}

