package com.xiaoyv404.mirai

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.fail
import kotlin.reflect.jvm.jvmName

internal open class TestBase : DatabaseTest() {
    internal inline fun <reified T> assertIsInstance(value: Any?, block: T.() -> Unit = {}) {
        if (value !is T) {
            fail { "Actual value $value (${value?.javaClass}) is not instanceof ${T::class.jvmName}" }
        }
        block(value)
    }

    internal fun runTest(action: suspend CoroutineScope.() -> Unit) {
        runBlocking(block = action)
    }
}