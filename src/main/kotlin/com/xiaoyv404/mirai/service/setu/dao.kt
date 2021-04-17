package com.xiaoyv404.mirai.service.setu

import kotlinx.serialization.Serializable

fun sequenceInformation(ii: ImageInfo): String =
    "��ƷID: ${ii.id}\n" +
        "����: ${ii.title}\n" +
        "��ǩ: ${ii.tags}\n" +
        "ͼƬ��: ${ii.picturesMun}\n" +
        "��������: ${ii.userName}\n" +
        "����ID: ${ii.userId}"

data class ImageInfo(
    val id: Long,
    val picturesMun: Int,
    val title: String,
    val tags: String,
    val userId: String,
    val userName: String,
)

@Serializable
data class PixivJson(
    val alt: String,
    val createDate: String,
    val description: String,
    val id: String,
    val illustComment: String,
    val illustId: String,
    val illustTitle: String,
    val illustType: Int,
    val restrict: Int,
    val sl: Int,
    val storableTags: List<String>,
    val tags: Tags,
    val title: String,
    val uploadDate: String,
    val urls: Urls,
    val userAccount: String,
    val userId: String,
    val userName: String,
    val xRestrict: Int,
)

@Serializable
data class Tags(
    val authorId: String,
    val isLocked: Boolean,
    val tags: List<Tag>,
    val writable: Boolean,
)

@Serializable
data class Urls(
    val mini: String,
    val original: String,
    val regular: String,
    val small: String,
    val thumb: String,
)

@Serializable
data class Tag(
    val deletable: Boolean,
    val locked: Boolean,
    val tag: String,
)