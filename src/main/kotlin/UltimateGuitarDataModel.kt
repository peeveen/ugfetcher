package com.stevenfrew.ultimateguitar

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UltimateGuitarStoreResult<T>(val page: UltimateGuitarStorePageResult<T>)

@Serializable
data class UltimateGuitarStorePageResult<T>(val data: T)

@Serializable
open class SearchResult(val store: UltimateGuitarStoreResult<SearchResultStorePageData>)

@Serializable
data class SearchResultStorePageData(val results: List<TabInfo>)

@Serializable
data class TabInfo(
	@SerialName("song_name")
	val songName: String,
	@SerialName("artist_name")
	val artistName: String,
	@SerialName("tab_url")
	val tabUrl: String,
	@SerialName("tonality_name")
	val key: String = "",
	@SerialName("username")
	val creator: String? = null,
	val votes: Int = 0,
	val type: String? = null,
	val version: Int = 0,
	val rating: Double = 0.0,
) {
	companion object {
		internal const val UNREGISTERED_USER = "Unregistered"
	}
}

@Serializable
open class SongResult(val store: UltimateGuitarStoreResult<SongResultStorePageData>)

@Serializable
data class SongResultStorePageData(
	@SerialName("tab")
	val tabInfo: TabInfo,
	@SerialName("tab_view")
	val tabView: TabView
)

@Serializable
data class TabView(
	@SerialName("wiki_tab")
	val wikiTab: TabViewWikiTab,
	val meta: TabViewMeta? = null
)

@Serializable
data class TabViewMeta(
	val capo: Int = 0,
	val tuning: Tuning? = null
)

@Serializable
data class Tuning(
	val name: String? = null,
	val value: String? = null
) {
	companion object {
		internal const val STANDARD_TUNING_NAME = "Standard"
	}
}

@Serializable
data class TabViewWikiTab(
	val content: String
)
