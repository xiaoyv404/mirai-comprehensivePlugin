package com.xiaoyv404.mirai.databace

class BiliBili {
    //视频表达式
    val biliBvFind = "(BV|bv)+1[a-zA-Z1-9]{2}4[a-zA-Z1-9]1[a-zA-Z1-9]7[a-zA-Z1-9]{2}".toRegex()
    val biliAvFind = "(av|AV)[1-9]\\d{0,18}".toRegex()

    //b23站短
    val b23Find = "(https?://b23.tv/\\S{6})".toRegex()

    //格式化防大量回车
    val deleteEnter = "(\\cJ\\cJ)+".toRegex()
}

class Pixiv {
    val worksInfoFind = "(?=\\{\"illustId\":\").*?(?=,\"userIllusts\")".toRegex()
    val worksNumberFind =
        "(?<=<p>這個作品ID中有 )[1-9]*(?= 張圖片，需要指定是第幾張圖片才能正確顯示\\(請參考<a href=\"https://pixiv.cat/\">首頁</a>說明\\)。</p>)".toRegex()
    val setu = "(?<=来)[0-9]*(?=份涩图)".toRegex()
}
