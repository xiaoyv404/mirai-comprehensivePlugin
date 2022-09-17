package com.xiaoyv404.mirai.app.webAPI.controller

enum class ResultEnum(override val code: Int, override val msg: String) : IResult {
    SUCCESS(2001, "接口调用成功"),
    VALIDATE_FAILED(2002, "参数校验失败"),
    COMMON_FAILED(2003, "接口调用失败"),
    FORBIDDEN(2004, "没有权限访问资源");
}
@kotlinx.serialization.Serializable
data class NfResult<T>(val code: Int, val msg: String, val data: T) {
    companion object{
        fun <T> success(data: T): NfResult<T> {
            return NfResult(ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg, data)
        }

        fun <T> success(msg: String, data: T): NfResult<T> {
            return NfResult(ResultEnum.SUCCESS.code, msg, data)
        }

        fun failed(): NfResult<Any?> {
            return NfResult(ResultEnum.COMMON_FAILED.code, ResultEnum.COMMON_FAILED.msg, null)
        }

        fun failed(msg: String): NfResult<Any?> {
            return NfResult(ResultEnum.COMMON_FAILED.code, msg, null)
        }

        fun failed(errorResult: IResult): NfResult<Nothing?> {
            return NfResult(errorResult.code, errorResult.msg, null)
        }

        fun <T> instance(code: Int, message: String, data: T): NfResult<T> {
            return NfResult(code, message, data)
        }
    }
}