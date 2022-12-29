package com.xiaoyv404.mirai.app.webAPI.router.mincreaftServer

import io.ktor.server.routing.*

fun Route.minecraftSeverRouter(){
    route("/minecraftSever"){
        getAllServer()
    }
}