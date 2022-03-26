package com.xiaoyv404.mirai.extension

import org.ktorm.database.Database
import org.ktorm.expression.SqlExpression
import org.ktorm.expression.SqlFormatter
import org.ktorm.support.postgresql.PostgreSqlDialect
import org.ktorm.support.postgresql.PostgreSqlFormatter

open class MyPostgreSqlDialect : PostgreSqlDialect() {

    override fun createSqlFormatter(database: Database, beautifySql: Boolean, indentSize: Int): SqlFormatter {
        return MyPostgreSqlFormatter(database, beautifySql, indentSize)
    }
}

class MyPostgreSqlFormatter(database: Database, beautifySql: Boolean, indentSize: Int)
    : PostgreSqlFormatter(database, beautifySql, indentSize) {

    override fun visitUnknown(expr: SqlExpression): SqlExpression {
        return when (expr) {
            is AsJsonExpression -> {
                if (expr.left.removeBrackets) {
                    visit(expr.left)
                } else {
                    write("(")
                    visit(expr.left)
                    removeLastBlank()
                    write(") ")
                }

                // if already json, then do nothing, just make compiler happy
                if (!expr.alreadyJson) {
                    removeLastBlank()
                    write("::jsonb ")
                }
                expr
            }
            is JsonAccessAsTextExpression<*> -> {
                // Json only, no need to add brackets
                visit(expr.left)

                write("->> ")

                visit(expr.right)

                expr
            }
            is JsonAccessExpression<*> -> {
                // Json only, no need to add brackets
                visit(expr.left)

                write("-> ")

                visit(expr.right)

                expr
            }

            is JsonFindOrNotExpression<*> ->{
                visit(expr.left)

                write("?? ")

                visit(expr.right)

                expr
            }
            else -> {
                super.visitUnknown(expr)
            }
        }
    }
}