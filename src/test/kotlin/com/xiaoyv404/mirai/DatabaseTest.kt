package com.xiaoyv404.mirai

import com.xiaoyv404.mirai.extension.MyPostgreSqlDialect
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.ktorm.database.Database
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.mockito.Mockito
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.BeforeTest

@Testcontainers
internal open class DatabaseTest {
    @Container
    private val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:latest").withDatabaseName("404")
        .withUsername("test")
        .withPassword("test")
    @BeforeTest
    open fun init() {
        com.xiaoyv404.mirai.database.Database.apply {
            db =
                Database.connect(
                    HikariDataSource(HikariConfig().apply {
                        jdbcUrl = postgres.jdbcUrl
                        driverClassName = "org.postgresql.Driver"
                        username = postgres.username
                        password = postgres.password
                        maximumPoolSize = 10
                    }),
                    alwaysQuoteIdentifiers = true,
                    logger = ConsoleLogger(threshold = LogLevel.INFO),
                    dialect = MyPostgreSqlDialect()
                )
            rdb = Mockito.mock()
            execSqlScript("init-data.sql")
        }
    }
    internal fun execSqlScript(filename: String) {
        com.xiaoyv404.mirai.database.Database.db.useConnection { conn ->
            conn.createStatement().use { statement ->
                javaClass.classLoader
                    ?.getResourceAsStream(filename)
                    ?.bufferedReader()
                    ?.use { reader ->
                        for (sql in reader.readText().split(';')) {
                            if (sql.any { it.isLetterOrDigit() }) {
                                statement.executeUpdate(sql)
                            }
                        }
                    }
            }
        }
    }
}