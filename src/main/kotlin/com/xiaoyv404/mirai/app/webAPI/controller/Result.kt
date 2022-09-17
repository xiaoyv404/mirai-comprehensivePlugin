package com.xiaoyv404.mirai.app.webAPI.controller

enum class ResultEnum(override val code: Int, override val msg: String) : IResult {
    SUCCESS(2001, "接口调用成功"),
    VALIDATE_FAILED(2002, "参数校验失败"),
    COMMON_FAILED(2003, "接口调用失败"),
    FORBIDDEN(2004, "没有权限访问资源");
}

class Result<T>(code: Int, msg: String, data: T) {
    fun <T> success(data: T): Result<T> {
        return Result(ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg, data)
    }

    fun <T> success(message: String, data: T): Result<T> {
        return Result(ResultEnum.SUCCESS.code, message, data)
    }

    fun failed(): Result<T?> {
        return Result(ResultEnum.COMMON_FAILED.code, ResultEnum.COMMON_FAILED.msg, null)
    }

    fun failed(message: String): Result<T?> {
        return Result(ResultEnum.COMMON_FAILED.code, message, null)
    }

    fun failed(errorResult: IResult): Result<T?> {
        return Result(errorResult.code, errorResult.msg, null)
    }

    fun <T> instance(code: Int, message: String, data: T): Result<T> {
        return Result(code, message, data)
    }
}