package com.xiaoyv404.mirai.app.webAPI.controller

enum class ResultEnum(override val code: Int, override val msg: String) : IResult {
    SUCCESS(2001, "接口调用成功"),
    VALIDATE_FAILED(2002, "参数校验失败"),
    COMMON_FAILED(2003, "接口调用失败"),
    FORBIDDEN(2004, "没有权限访问资源");
}