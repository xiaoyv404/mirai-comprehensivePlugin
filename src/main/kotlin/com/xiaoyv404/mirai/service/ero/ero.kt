package com.xiaoyv404.mirai.service.ero

import com.xiaoyv404.mirai.service.ero.localGallery.localGalleryListener
import io.ktor.util.*

const val setuAPIUrl = "https://api.ixiaowai.cn/api/api.php"

// TODO: 2021/7/21 �޸ĳ������лỰ�����Է��ʣ������Ҫ�޸�һ���ڳ���group��Ȩ�޿���

@KtorExperimentalAPI
fun eroEntrance() {
    localGalleryListener()
//    searchListenerRegister()
}