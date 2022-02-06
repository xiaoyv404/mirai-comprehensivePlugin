package com.xiaoyv404.mirai.service.ero

import com.xiaoyv404.mirai.service.ero.localGallery.localGalleryListener
import com.xiaoyv404.mirai.service.ero.sauceNao.searchListenerRegister

const val setuAPIUrl = "https://api.ixiaowai.cn/api/api.php"

// TODO: 2021/7/21 修改成了所有会话都可以访问，因此需要修复一下在除了group的权限控制

fun eroEntrance() {
    localGalleryListener()
    searchListenerRegister()
}