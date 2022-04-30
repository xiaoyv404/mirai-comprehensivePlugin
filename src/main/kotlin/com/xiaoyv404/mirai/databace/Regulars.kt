package com.xiaoyv404.mirai.databace

object Pixiv {
    val worksInfoFind = Regex("(?=\\{\"illustId\":\").*?(?=,\"userIllusts\")")
    val worksNumberFind = Regex("(?<=<p>這個作品ID中有 )[0-9]+(?= 張圖片，需要指定是第幾張圖片才能正確顯示\\(請參考<a href=\"https://pixiv.cat/\">首頁</a>說明\\)。</p>)")
}