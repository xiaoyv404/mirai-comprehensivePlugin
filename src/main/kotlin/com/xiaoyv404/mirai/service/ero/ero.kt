package com.xiaoyv404.mirai.service.ero

import com.xiaoyv404.mirai.service.ero.localGallery.localGalleryListener
import com.xiaoyv404.mirai.service.ero.sauceNao.searchListenerRegister

const val setuAPIUrl = "https://api.ixiaowai.cn/api/api.php"

// TODO: 2021/7/21 �޸ĳ������лỰ�����Է��ʣ������Ҫ�޸�һ���ڳ���group��Ȩ�޿���

fun eroEntrance() {
    localGalleryListener()
    searchListenerRegister()
}