package com.xiaoyv404.mirai.app.webAPI.router.admin.core

import io.ktor.server.routing.*

fun Route.coreRouter(){
    route("/core") {
        getOnlineApps()
    }
}