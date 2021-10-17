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
    val thesaurusRemove = Regex("^(404 thesaurus remove( ((-h|--help)|([0-9]+)))?)\$")

    val ero = Regex("^((��)([0-9]*)(��ɬͼ))")
    val ban = Regex("^(404 ban ((-h\$|--help\$)|(([0-9]+) ([0-9]+) (([0-9]+)|unban))))\$")
    val join = Regex("^(404 join ((-h\$|--help\$)|( [0-9]+)))\$")
    val bugReport = Regex("^(404 report bug ((-h\$|--help\$)|((\\w+) ((?:.|\\n)+))))\$")
    val eroAdd = Regex("^((404 ero add|���ɬͼ) ((-h\$|--help\$)|([0-9]+)))\$")
    val eroRemove = Regex("^((404 ero remove|ɾ��ɬͼ) ((-h\$|--help\$)|([0-9]+)))\$")
    val eroSearch = Regex("^((404 ero search|��ɬͼ) ((-h\$|--help\$)|(.+)))\$")
    val addBot = Regex("^((404 add bot|��ӻ�����) ((-h|--help)|(@*[0-9]+)))\$")
    val minecraftServerStats =
        Regex("^(404 (((������|����|����ͷ)(����û|״̬))|((?i)((Server|Potato)Status)))( ((-h|--help)|(-p)))?)\$")
    val getBiliBiliUpInformation = Regex("^(404 (?i)(��ȡup��Ϣ|(getUpInformation))( ((-h|--help)|([0-9]+)))?)\$")

    val SauceNao = Regex("^((404 img search|��ͼ)( (-h\$|--help\$)|(.*))?)\$")

    val debuMe= Regex("^(~me(.*))$")
}