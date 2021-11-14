package com.xiaoyv404.mirai.service.tool

import com.xiaoyv404.mirai.service.bilibili.VideoDataJson
import com.xiaoyv404.mirai.service.bilibili.tID

//格式化视频信息
fun parsingVideoDataString(pJson: VideoDataJson): String {
    val data = pJson.data
    return (
"""${data.pic}

标题: ${data.title}
视频分区: ${tID[data.tid]}

视频简介: ${data.desc}

${data.stat.view}播放, ${data.videos}分p, ${data.stat.danmaku}弹幕, ${data.stat.reply}评论
${data.stat.favorite}收藏, ${data.stat.share}分享, ${data.stat.coin}投币, ${data.stat.like}点赞

UP: ${data.owner.name}  UID: ${data.owner.mid}
空间链接: https://space.bilibili.com/${data.owner.mid}

av${data.aid}  ${data.bvid}
https://www.bilibili.com/video/av${data.aid}""")
}