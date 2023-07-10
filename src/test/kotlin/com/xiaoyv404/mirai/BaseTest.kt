package com.xiaoyv404.mirai

import com.xiaoyv404.mirai.extension.MyPostgreSqlDialect
import com.xiaoyv404.mirai.tool.CommandSplit
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.mock.MockBotFactory
import net.mamoe.mirai.mock.utils.MockActionsScope
import net.mamoe.mirai.mock.utils.broadcastMockEvents
import org.junit.jupiter.api.AfterEach
import org.ktorm.database.Database
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.mockito.Mockito
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.test.BeforeTest

@Testcontainers
internal abstract class BaseTest : TestBase() {
//    @Container
//    var redis: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:7.0.11-alpine"))
    @Container
    var postgres: GenericContainer<*> = GenericContainer(DockerImageName.parse("postgres")).withExposedPorts(2001)

    internal val bot = MockBotFactory.newMockBotBuilder()
        .id(2079373402)
        .nick("404")
        .create()

    @BeforeTest
    open fun init() {
        com.xiaoyv404.mirai.databace.Database.apply {
            db =
                Database.connect(
                    "jdbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/404",
                    alwaysQuoteIdentifiers = true,
                    logger = ConsoleLogger(threshold = LogLevel.TRACE),
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
        action: suspend MessageEvent.() -> Unit
    ): List<Event> {
        val result = mutableListOf<Event>()
        val listener = GlobalEventChannel.subscribeAlways<Event> {
            result.add(this)
        }
        this.filterIsInstance<MessageEvent>().forEach {
            action(it)
        }
        listener.cancel()
        return result
    }

    internal suspend fun List<Event>.runIFsApp(
        action: suspend (array: Array<String>, msg: MessageEvent) -> Boolean
    ): List<Event> {
        val result = mutableListOf<Event>()
        val listener = GlobalEventChannel.subscribeAlways<Event> {
            result.add(this)
        }
        this.filterIsInstance<MessageEvent>().forEach { msg ->
            CommandSplit.splitWhit404(msg.message.contentToString())?.let {
                action(it.toTypedArray(), msg)
            }
        }
        listener.cancel()
        return result
    }

    @AfterEach
    internal fun `$$bot dispose`() {
        bot.close()
    }
}