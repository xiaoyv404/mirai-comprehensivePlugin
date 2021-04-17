package com.xiaoyv404.mirai.service.tool

import com.xiaoyv404.mirai.service.bilibili.VideoDataJson
import com.xiaoyv404.mirai.service.bilibili.regular
import com.xiaoyv404.mirai.service.bilibili.tID

//格式化视频信息
fun parsingVideoDataString(pJson: VideoDataJson): String {
    val data = pJson.data
    return (
        "\n${data.pic}" +
            "\n\n标题: ${data.title}" +
            "\n视频分区: ${tID[data.tid]}" +
            "\n\n视频简介: ${data.desc.replace(regular.deleteEnter, "\n")}" +
            "\n\n${data.stat.view}播放, ${data.videos}分p, ${data.stat.danmaku}弹幕, ${data.stat.reply}评论" +
            "\n${data.stat.favorite}收藏, ${data.stat.share}分享, ${data.stat.coin}投币, ${data.stat.like}点赞" +
            "\n\nUP: ${data.owner.name}  UID: ${data.owner.mid}" +
            "\n空间链接: https://space.bilibili.com/${data.owner.mid}" +
            "\n\nav${data.aid}  ${data.bvid}" +
            "\nhttps://www.bilibili.com/video/av${data.aid}"
        )
}