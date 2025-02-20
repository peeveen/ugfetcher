package com.stevenfrew.ultimateguitar

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object ChordSearcher {
	private const val CHORDS_TYPE = "Chords"

	@OptIn(InternalSerializationApi::class)
	fun search(searchString: String): List<TabInfo> {
		val urlEncodedSearchString = URLEncoder.encode(searchString, StandardCharsets.UTF_8.toString())
		val urlString = "${DataFetcher.ULTIMATE_GUITAR_HOST}/search.php?search_type=title&value=${urlEncodedSearchString}"

		val storePageData = DataFetcher.get(urlString, SearchResult::class.serializer())
		val results = storePageData?.store?.page?.data?.results ?: listOf()
		return results.filter { it.type == CHORDS_TYPE }
	}
}