package com.xiaoyv404.mirai

import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.mock.MockBotFactory
import net.mamoe.mirai.mock.utils.MockActionsScope
import net.mamoe.mirai.mock.utils.broadcastMockEvents
import org.junit.jupiter.api.AfterEach
import org.ktorm.database.Database
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.mockito.Mockito
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.test.BeforeTest


internal abstract class BaseTest : TestBase() {
    internal val bot = MockBotFactory.newMockBotBuilder()
        .id(2079373402)
        .nick("404")
        .create()

    @BeforeTest
    open fun init() {
        com.xiaoyv404.mirai.databace.Database.apply {
            db =
                Database.connect(
                    "jdbc:h2:mem:ktorm;DB_CLOSE_DELAY=-1",
                    alwaysQuoteIdentifiers = true,
                    logger = ConsoleLogger(threshold = LogLevel.INFO)
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

    @AfterEach
    internal fun `$$bot dispose`() {
        bot.close()
    }
}