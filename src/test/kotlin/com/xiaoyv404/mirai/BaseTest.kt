package com.xiaoyv404.mirai

import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.NfAppMessageHandler
import com.xiaoyv404.mirai.extension.MyPostgreSqlDialect
import com.xiaoyv404.mirai.tool.CommandSplit
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.mock.MockBotFactory
import net.mamoe.mirai.mock.utils.MockActionsScope
import net.mamoe.mirai.mock.utils.broadcastMockEvents
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.ktorm.database.Database
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.mockito.Mockito
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.test.BeforeTest

@Testcontainers
internal abstract class BaseTest : TestBase() {
    @Container
    private val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:latest").withDatabaseName("404")
        .withUsername("test")
        .withPassword("test")


    internal val bot = MockBotFactory.newMockBotBuilder()
        .id(2079373402)
        .nick("404")
        .create()

    internal val mockWebServer = MockWebServer()

    @BeforeTest
    open fun init() {
        mockWebServer.start();
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

    @OptIn(ExperimentalContracts::class)
    internal suspend fun runAndReceiveEventBroadcast(
        action: suspend MockActionsScope.() -> Unit
    ): List<Event> {
        contract {
            callsInPlace(action, InvocationKind.EXACTLY_ONCE)
        }

        val result = mutableListOf<Event>()
        val listener = GlobalEventChannel.subscribeAlways<Event> {
            result.add(this)
        }
        broadcastMockEvents {
            action()
        }
        listener.cancel()
        return result
    }

    internal suspend fun List<Event>.runNfAppMessageHandlerApp(
        action: NfAppMessageHandler
    ): List<Event> {
        val result = mutableListOf<Event>()
        val listener = GlobalEventChannel.subscribeAlways<Event> {
            result.add(this)
        }
        this.filterIsInstance<MessageEvent>().forEach {
            action.handleMessage(it)
        }
        listener.cancel()
        return result
    }

    internal suspend fun List<Event>.runIFsApp(
        action: IFshApp
    ): List<Event> {
        val result = mutableListOf<Event>()
        val listener = GlobalEventChannel.subscribeAlways<Event> {
            result.add(this)
        }
        this.filterIsInstance<MessageEvent>().forEach { msg ->
            CommandSplit.splitWhit404(msg.message.contentToString())?.let {
                action.executeRsh(it.toTypedArray(), msg)
            }
        }
        listener.cancel()
        return result
    }

    @AfterEach
    internal fun `$$bot dispose`() {
        mockWebServer.shutdown()
        bot.close()
    }
}