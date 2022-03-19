package com.xiaoyv404.mirai.databace

object Bilibili {
    //��Ƶ���ʽ
    val biliBvFind = "BV1[1-9A-NP-Za-km-z]{9}".toRegex()
    val biliAvFind = "(av|AV)([1-9]\\d{0,18})".toRegex()

    //b23վ��
    val b23Find = Regex("(https?://b23.tv/\\S{6})")
}

object Pixiv {
    val worksInfoFind = Regex("(?=\\{\"illustId\":\").*?(?=,\"userIllusts\")")
    val worksNumberFind = Regex("(?<=<p>�@����ƷID���� )[0-9]+(?= ���DƬ����Ҫָ���ǵڎ׏��DƬ�������_�@ʾ\\(Ո����<a href=\"https://pixiv.cat/\">���</a>�f��\\)��</p>)")
}

object Command {
    val ban = Regex("^(404 ban ((-h\$|--help\$)|(([0-9]+) ([0-9]+) (([0-9]+)|unban))))\$")
    val join = Regex("^(404 join ((-h\$|--help\$)|( [0-9]+)))\$")
    val addBot = Regex("^((404 add bot|��ӻ�����) (@?([0-9]+)))\$")
    val getBiliBiliUpInformation = Regex("^(404 (?i)(��ȡup��Ϣ|(getUpInformation))( ((-h|--help)|([0-9]+)))?)\$")

    val debuMe= Regex("^(~me(.*))$")

    val dice = Regex("^((\\.r)( (\\d+)((\\((\\d+)\\))|(:(\\d+)))?)?)\$")
}