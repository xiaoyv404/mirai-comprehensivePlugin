package com.xiaoyv404.mirai.app.ero.sauceNao

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SauceNaoResponse(
    // 返回值判断查询是否成功
    val header: Header,
    val results: List<Result>
) {
    @Serializable
    data class Header(
        val user_id: String,
    )

    @Serializable
    data class Result(
        val header: ResultsHeader,
        val data: ResultsData
    )

    @Serializable
    data class ResultsHeader(
        val similarity: String, // 相似度
        val thumbnail: String, // 直接下载缩略图的链接
        val index_id: Int, // 数据库id
        val index_name: String // 数据库名称
    )

    @Serializable
    data class ResultsData(
        val danbooru_id: String? = null,
        val gelbooru_id: String? = null,
        val drawr_id: String? = null,
        val pixiv_id: String? = null,
        val seiga_id: String? = null,
        val yandere_id: String? = null,
        val konachan_id: String? = null,
        val sankaku_id: String? = null,
        @SerialName("anime-pictures_id")
        val animepictures_id: String? = null,
        val e621_id: String? = null,
        val idol_id: String? = null,
        val imdb_id: String? = null,
        val anidb_aid: String? = null,
        val bcy_id: String? = null,
        val ddb_id: String? = null,
        val nijie_id: String? = null,
        val getchu_id: String? = null,
        val shutterstock_id: String? = null,
        val contributor_id: String? = null,
        val est_time: String? = null,
        val bcy_type: String? = null,
        val da_id: String? = null,
        val pg_id: String? = null,
        val mal_id: String? = null,
        val md_id: String? = null,
        val mu_id: String? = null,
        val pawoo_id: String? = null,
        val pawoo_user_acct: String? = null,
        val pawoo_user_username: String? = null,
        val pawoo_user_display_name: String? = null,
        val title: String? = null,
        val jp_title: String? = null,
        val eng_title: String? = null,
        val alt_titles: List<String> = emptyList(),
        val jp_name: String? = null,
        val eng_name: String? = null,
        val creator: String? = null,
        val material: String? = null,
        val member_name: String? = null,
        val member_id: String? = null,
        val part: String? = null,
        val part_name: String? = null,
        val date: String? = null,
        val company: String? = null,
        val file: String? = null,
        val year: String? = null,
        val member_link_id: String? = null,
        val author_name: String? = null,
        val author_url: String? = null,
        val characters: String? = null,
        val source: String? = null,
        val url: String? = null,
        val type: String? = null,
        val created_at: String? = null,
        val ext_urls: List<String> = emptyList()
    )
}