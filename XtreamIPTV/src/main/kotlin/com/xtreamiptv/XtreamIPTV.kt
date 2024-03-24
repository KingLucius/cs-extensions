package com.xtreamiptv

import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.AppUtils.parseJson
import com.lagradost.cloudstream3.utils.AppUtils.toJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.INFER_TYPE
import com.lagradost.cloudstream3.utils.Qualities

class XtreamIPTV : MainAPI() {
    override var lang = "en"
    override var name = "Xtream IPTV (Live)"
    override val usesWebView = false
    override val hasMainPage = true
    override val hasDownloadSupport = false

    override val supportedTypes =
        setOf(
            TvType.Live
        )

    private lateinit var apiURL: String
    private lateinit var serverUrlWithData: String

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {

        val serverData = mainUrl.split(",")
        val serverUrl = serverData[0]
        val user = serverData[1]
        val pass = serverData[2]

        apiURL = "${serverUrl}/player_api.php?username=${user}&password=${pass}"
        serverUrlWithData = "${serverUrl}/${user}/${pass}/"

        val cats = mutableListOf<HomePageList>()

        val parsedCategories = parseJson<List<Category>>(loadLiveCategories())
        val parsedStreams = parseJson<List<Stream>>(loadLiveStreams())

        if (page <= 1) {
            parsedCategories.map { cat ->
                val tempStreamList = mutableListOf<SearchResponse>()
                parsedStreams.map { stream ->

                    if (stream.category_id == cat.category_id) {
                        tempStreamList.add(
                            LiveSearchResponse(
                                name = stream.name,
                                url = Data(
                                    num = stream.num,
                                    name = stream.name,
                                    stream_type = stream.stream_type,
                                    stream_id = stream.stream_id,
                                    stream_icon = stream.stream_icon,
                                    epg_channel_id = stream.epg_channel_id,
                                    added = stream.added,
                                    is_adult = stream.is_adult,
                                    category_id = stream.category_id,
                                    custom_sid = stream.custom_sid,
                                    tv_archive = stream.tv_archive,
                                    direct_source = stream.direct_source,
                                    tv_archive_duration = stream.tv_archive_duration,
                                ).toJson(),
                                apiName= this@XtreamIPTV.name,
                                type= TvType.Live,
                                posterUrl= stream.stream_icon,
                            )
                        )
                    }
                }
                cats.add(HomePageList(cat.category_name, tempStreamList, false))
            }
        }
        return newHomePageResponse(cats)
    }

    override suspend fun load(url: String): LoadResponse {

        val data = parseJson<Data>(url)

        return newMovieLoadResponse(
            name =  data.name,
            url = url,
            dataUrl = url,
            type = TvType.Live,
        ) {
            this.posterUrl = data.stream_icon
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {

        val parsedData = parseJson<Data>(data)

        callback.invoke(
            ExtractorLink(
                source = parsedData.name,
                name = parsedData.name,
                url = serverUrlWithData + parsedData.stream_id.toString(),
                referer = "",
                quality = Qualities.Unknown.value,
                type = INFER_TYPE,
            )
        )
        return true
    }

    private suspend fun loadLiveCategories(): String {
        return app.get("$apiURL&action=get_live_categories").body.string()
    }

    private suspend fun loadLiveStreams(): String {
        return app.get("$apiURL&action=get_live_streams").body.string()
    }

    data class Category(
        @JsonProperty("category_id") val category_id: String,
        @JsonProperty("category_name") val category_name: String,
        @JsonProperty("parent_id") val parent_id: Int,
    )

    data class Stream(
        @JsonProperty("num") val num: Int,
        @JsonProperty("name") val name: String,
        @JsonProperty("stream_type") val stream_type: String? = null,
        @JsonProperty("stream_id") val stream_id: Int,
        @JsonProperty("stream_icon") val stream_icon: String? = null,
        @JsonProperty("epg_channel_id") val epg_channel_id: String? = null,
        @JsonProperty("added") val added: String? = null,
        @JsonProperty("is_adult") val is_adult: String? = null,
        @JsonProperty("category_id") val category_id: String,
        @JsonProperty("custom_sid") val custom_sid: String? = null,
        @JsonProperty("tv_archive") val tv_archive: Int,
        @JsonProperty("direct_source") val direct_source: String? = null,
        @JsonProperty("tv_archive_duration") val tv_archive_duration: Int,
    )

    data class Data(
        val num: Int,
        val name: String,
        val stream_type: String? = null,
        val stream_id: Int,
        val stream_icon: String? = null,
        val epg_channel_id: String? = null,
        val added: String? = null,
        val is_adult: String? = null,
        val category_id: String,
        val custom_sid: String? = null,
        val tv_archive: Int,
        val direct_source: String? = null,
        val tv_archive_duration: Int,
    )
}