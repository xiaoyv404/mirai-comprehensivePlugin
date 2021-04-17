package com.xiaoyv404.mirai.service.tool

import com.xiaoyv404.mirai.service.bilibili.VideoDataJson
import com.xiaoyv404.mirai.service.bilibili.regular
import com.xiaoyv404.mirai.service.bilibili.tID

//��ʽ����Ƶ��Ϣ
fun parsingVideoDataString(pJson: VideoDataJson): String {
    val data = pJson.data
    return (
        "\n${data.pic}" +
            "\n\n����: ${data.title}" +
            "\n��Ƶ����: ${tID[data.tid]}" +
            "\n\n��Ƶ���: ${data.desc.replace(regular.deleteEnter, "\n")}" +
            "\n\n${data.stat.view}����, ${data.videos}��p, ${data.stat.danmaku}��Ļ, ${data.stat.reply}����" +
            "\n${data.stat.favorite}�ղ�, ${data.stat.share}����, ${data.stat.coin}Ͷ��, ${data.stat.like}����" +
            "\n\nUP: ${data.owner.name}  UID: ${data.owner.mid}" +
            "\n�ռ�����: https://space.bilibili.com/${data.owner.mid}" +
            "\n\nav${data.aid}  ${data.bvid}" +
            "\nhttps://www.bilibili.com/video/av${data.aid}"
        )
}