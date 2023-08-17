package com.xiaoyv404.mirai

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.fail
import kotlin.reflect.jvm.jvmName

internal open class TestBase {
    internal inline fun <reified T> assertIsInstance(value: Any?, block: T.() -> Unit = {}) {
        if (value !is T) {
            fail { "Actual value $value (${value?.javaClass}) is not instanceof ${T::class.jvmName}" }
        }
        block(value)
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
    internal fun runTest(action: suspend CoroutineScope.() -> Unit) {
        runBlocking(block = action)
    }
}