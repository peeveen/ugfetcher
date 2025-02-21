package com.github.peeveen.ultimateguitar

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import org.jsoup.HttpStatusException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Singleton class for getting UG search results.
 */
object ChordSearcher {
	private const val CHORDS_TYPE = "Chords"

	/**
	 * Searches the UG database for entries that match the given search string.
	 *
	 * @param searchString The string to search for.
	 */
	@OptIn(InternalSerializationApi::class)
	fun search(searchString: String): List<TabInfo> {
		val urlEncodedSearchString = URLEncoder.encode(searchString, StandardCharsets.UTF_8.toString())
		val urlString = "${DataFetcher.ULTIMATE_GUITAR_HOST}/search.php?search_type=title&value=${urlEncodedSearchString}"

		try {
			val storePageData = DataFetcher.get(urlString, SearchResult::class.serializer())
			val results = storePageData?.store?.page?.data?.results ?: listOf()
			return results.filter { it.type == CHORDS_TYPE }
		} catch (httpEx: HttpStatusException) {
			if (httpEx.statusCode == 404)
				return listOf()
			throw httpEx
		}
	}
}