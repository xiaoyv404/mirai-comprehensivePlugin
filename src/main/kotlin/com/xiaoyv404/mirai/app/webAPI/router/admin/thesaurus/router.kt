package com.xiaoyv404.mirai.app.webAPI.router.admin.thesaurus

import io.ktor.server.routing.*

fun Route.thesaurusRouter(){
    route("thesaurus"){
        finByQuestion()
    }
}