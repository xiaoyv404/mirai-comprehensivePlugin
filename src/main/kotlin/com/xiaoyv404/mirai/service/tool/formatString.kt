package com.xiaoyv404.mirai.service.tool

import com.xiaoyv404.mirai.service.bilibili.VideoDataJson
import com.xiaoyv404.mirai.service.bilibili.tID

//��ʽ����Ƶ��Ϣ
fun parsingVideoDataString(pJson: VideoDataJson): String {
    val data = pJson.data
    return (
"""${data.pic}

����: ${data.title}
��Ƶ����: ${tID[data.tid]}

��Ƶ���: ${data.desc}

${data.stat.view}����, ${data.videos}��p, ${data.stat.danmaku}��Ļ, ${data.stat.reply}����
${data.stat.favorite}�ղ�, ${data.stat.share}����, ${data.stat.coin}Ͷ��, ${data.stat.like}����

UP: ${data.owner.name}  UID: ${data.owner.mid}
�ռ�����: https://space.bilibili.com/${data.owner.mid}

av${data.aid}  ${data.bvid}
https://www.bilibili.com/video/av${data.aid}""")
}