package com.xiaoyv404.mirai.app.ero

import com.xiaoyv404.mirai.app.ero.localGallery.localGalleryListener
import com.xiaoyv404.mirai.app.ero.sauceNao.searchListenerRegister

const val setuAPIUrl = "https://api.ixiaowai.cn/api/api.php"

// TODO: 2021/7/21 �޸ĳ������лỰ�����Է��ʣ������Ҫ�޸�һ���ڳ���group��Ȩ�޿���

fun eroEntrance() {
    localGalleryListener()
    searchListenerRegister()
}