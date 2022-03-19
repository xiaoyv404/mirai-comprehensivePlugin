package com.xiaoyv404.mirai.extension

import org.ktorm.expression.ArgumentExpression
import org.ktorm.expression.ScalarExpression
import org.ktorm.schema.ColumnDeclaring
import org.ktorm.schema.IntSqlType
import org.ktorm.schema.SqlType
import org.ktorm.schema.VarcharSqlType

data class AsJsonExpression(
    val left: ScalarExpression<*>,
    override val sqlType: SqlType<Any> = JsonSqlType(Any::class.java),
    override val isLeafNode: Boolean = false,
    override val extraProperties: Map<String, Any> = mapOf(),
    val alreadyJson: Boolean = false
) : ScalarExpression<Any>()

/**
 * This function make a AsJsonExpression.
 * When alreadyJson is true, the expression does nothing, just to make compiler happy.
 */
fun ColumnDeclaring<*>.asJson(alreadyJson: Boolean = false): AsJsonExpression {
    return AsJsonExpression(asExpression(), alreadyJson = alreadyJson)
}

// Get as Json

data class JsonAccessExpression<T : Any>(
    val left: AsJsonExpression,
    val right: ArgumentExpression<T>,
    override val sqlType: SqlType<Any> = JsonSqlType(Any::class.java),
    override val isLeafNode: Boolean = false,
    override val extraProperties: Map<String, Any> = mapOf()
) : ScalarExpression<Any>()

operator fun AsJsonExpression.get(param: String): AsJsonExpression {
    return JsonAccessExpression(this, ArgumentExpression(param, VarcharSqlType)).asJson(true)
}

operator fun AsJsonExpression.get(param: Int): AsJsonExpression {
    return JsonAccessExpression(this, ArgumentExpression(param, IntSqlType)).asJson(true)
}

// Get as Text

data class JsonAccessAsTextExpression<T : Any>(
    val left: AsJsonExpression,
    val right: ArgumentExpression<T>,
    override val sqlType: SqlType<String> = VarcharSqlType,
    override val isLeafNode: Boolean = false,
    override val extraProperties: Map<String, Any> = mapOf()
) : ScalarExpression<String>()

fun AsJsonExpression.getAsString(param: String): JsonAccessAsTextExpression<String> {
    return JsonAccessAsTextExpression(this, ArgumentExpression(param, VarcharSqlType))
}

fun AsJsonExpression.getAsString(param: Int): JsonAccessAsTextExpression<Int> {
    return JsonAccessAsTextExpression(this, ArgumentExpression(param, IntSqlType))
}

data class JsonFindHaveOrNotExpression<T : Any>(
    val left: AsJsonExpression,
    val right: ArgumentExpression<T>,
    override val sqlType: SqlType<String> = VarcharSqlType,
    override val isLeafNode: Boolean = false,
    override val extraProperties: Map<String, Any> = mapOf()
): ScalarExpression<String>()

fun AsJsonExpression.findHaveOrNot(param: String): JsonFindHaveOrNotExpression<String>{
    return JsonFindHaveOrNotExpression(this, ArgumentExpression(param, VarcharSqlType))
}