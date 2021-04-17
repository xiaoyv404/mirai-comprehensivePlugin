package com.xiaoyv404.mirai.databace

class BiliBili {
    //��Ƶ���ʽ
    val biliBvFind = "(BV|bv)+1[a-zA-Z1-9]{2}4[a-zA-Z1-9]1[a-zA-Z1-9]7[a-zA-Z1-9]{2}".toRegex()
    val biliAvFind = "(av|AV)[1-9]\\d{0,18}".toRegex()

    //b23վ��
    val b23Find = "(https?://b23.tv/\\S{6})".toRegex()

    //��ʽ���������س�
    val deleteEnter = "(\\cJ\\cJ)+".toRegex()
}

class Pixiv {
    val worksInfoFind = "(?=\\{\"illustId\":\").*?(?=,\"userIllusts\")".toRegex()
    val worksNumberFind =
        "(?<=<p>�@����ƷID���� )[1-9]*(?= ���DƬ����Ҫָ���ǵڎ׏��DƬ�������_�@ʾ\\(Ո����<a href=\"https://pixiv.cat/\">���</a>�f��\\)��</p>)".toRegex()
    val setu = "(?<=��)[0-9]*(?=��ɬͼ)".toRegex()
}
