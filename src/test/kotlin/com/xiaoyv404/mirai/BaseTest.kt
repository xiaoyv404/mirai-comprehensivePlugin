package com.xiaoyv404.mirai

import com.xiaoyv404.mirai.app.fsh.IFshApp
import com.xiaoyv404.mirai.core.NfAppMessageHandler
import com.xiaoyv404.mirai.tool.CommandSplit
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.mock.MockBotFactory
import net.mamoe.mirai.mock.utils.MockActionsScope
import net.mamoe.mirai.mock.utils.broadcastMockEvents
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Testcontainers
internal abstract class BaseTest : TestBase() {
    internal val bot = MockBotFactory.newMockBotBuilder()
        .id(2079373402)
        .nick("404")
        .create()

    internal val mockWebServer = MockWebServer()


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