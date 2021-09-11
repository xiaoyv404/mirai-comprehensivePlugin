package com.xiaoyv404.mirai.service.tool

import org.ktorm.expression.ArgumentExpression
import org.ktorm.expression.FunctionExpression
import org.ktorm.schema.BooleanSqlType
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import org.ktorm.schema.VarcharSqlType

inline fun <reified T : Any> Column<*>.jsonExtractContains(
    path: String,
    item: T,
    sqlType: SqlType<T>
): FunctionExpression<Boolean> {
    val listSqlType = this.sqlType

    return FunctionExpression(
        functionName = "json_contains",
        arguments = listOf(
            FunctionExpression(
                functionName = "json_extract",
                arguments = listOf(asExpression(), ArgumentExpression(path, VarcharSqlType)),
                sqlType = sqlType
            ),
            FunctionExpression(
                functionName = "json_array",
                arguments = listOf(ArgumentExpression(item, sqlType)),
                sqlType = listSqlType
            )
        ),
        sqlType = BooleanSqlType
    )
}