package com.xiaoyv404.mirai.tool

import org.ktorm.expression.ArgumentExpression
import org.ktorm.expression.FunctionExpression
import org.ktorm.expression.ScalarExpression
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import org.ktorm.schema.VarcharSqlType

inline fun <reified T : Any> Column<*>.jsonExtract(
    sqlType: SqlType<T>,
    vararg path: String
): FunctionExpression<T> {
    val argument = mutableListOf<ScalarExpression<*>>(asExpression())
    path.forEach {
        argument.add(ArgumentExpression(it, VarcharSqlType))
    }

    return FunctionExpression(
        functionName = "json_extract_path",
        arguments = argument.toList(),
        sqlType = sqlType
    )

}

//inline fun <reified T : Any> Column<*>.jsonExtractContains(
//    item: T,
//    sqlType: SqlType<T>,
//    vararg path: String
//): FunctionExpression<Boolean> {
//    val listSqlType = this.sqlType
//
//    val argument = mutableListOf<ScalarExpression<*>>(asExpression())
//    path.forEach {
//        argument.add(ArgumentExpression(it, VarcharSqlType))
//    }
//
//    return FunctionExpression(
//        functionName = "json_contains",
//        arguments = listOf(
//            FunctionExpression(
//                functionName = "json_extract_path",
//                arguments = argument,
//                sqlType = sqlType
//            ),
//            FunctionExpression(
//                functionName = "json_array",
//                arguments = listOf(ArgumentExpression(item, sqlType)),
//                sqlType = listSqlType
//            )
//        ),
//        sqlType = BooleanSqlType
//    )
//}

//inline fun <reified T : Any> Column<*>.jsonSearch(
//    oneOrAll: String,
//    searchStr: T,
//    path: String,
//    sqlType: SqlType<T>
//): FunctionExpression<String> {
//    val listSqlType = this.sqlType
//
//    return FunctionExpression(
//        functionName = "json_search",
//        arguments = listOf(
//            ArgumentExpression(oneOrAll, VarcharSqlType),
//            FunctionExpression(
//                functionName = "json_array",
//                arguments = listOf(ArgumentExpression(searchStr, sqlType)),
//                sqlType = listSqlType
//            ),
//            ArgumentExpression(path, VarcharSqlType)
//        ),
//        sqlType = VarcharSqlType
//    )
//}