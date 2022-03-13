package com.xiaoyv404.mirai.databace

object Bilibili {
    //视频表达式
    val biliBvFind = "BV1[1-9A-NP-Za-km-z]{9}".toRegex()
    val biliAvFind = "(av|AV)([1-9]\\d{0,18})".toRegex()

    //b23站短
    val b23Find = Regex("(https?://b23.tv/\\S{6})")
}

object Pixiv {
    val worksInfoFind = Regex("(?=\\{\"illustId\":\").*?(?=,\"userIllusts\")")
    val worksNumberFind = Regex("(?<=<p>這個作品ID中有 )[0-9]+(?= 張圖片，需要指定是第幾張圖片才能正確顯示\\(請參考<a href=\"https://pixiv.cat/\">首頁</a>說明\\)。</p>)")
}

object Command {
    val ero = Regex("^((来)([0-9]*)(份涩图))")
    val ban = Regex("^(404 ban ((-h\$|--help\$)|(([0-9]+) ([0-9]+) (([0-9]+)|unban))))\$")
    val join = Regex("^(404 join ((-h\$|--help\$)|( [0-9]+)))\$")
    val eroAdd = Regex("^((404 ero add|添加涩图)( ((https://www\\.pixiv\\.net/artworks/)?(\\d+))+)?)( -n)?\$")
    val eroRemove = Regex("^((404 ero remove|删除涩图) ([0-9]+))\$")
    val eroSearch = Regex("^((404 ero search|搜涩图) (.+))\$")
    val addBot = Regex("^((404 add bot|添加机器人) (@?([0-9]+)))\$")
    val getBiliBiliUpInformation = Regex("^(404 (?i)(获取up信息|(getUpInformation))( ((-h|--help)|([0-9]+)))?)\$")

    val debuMe= Regex("^(~me(.*))$")

    val dice = Regex("^((\\.r)( (\\d+)((\\((\\d+)\\))|(:(\\d+)))?)?)\$")
}