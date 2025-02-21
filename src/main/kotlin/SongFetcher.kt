package com.github.peeveen.ultimateguitar

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

/**
 * Singleton with which to download chords/lyrics (so-called "tabs", even though they aren't
 * really "tablature").
 */
object SongFetcher {
	/**
	 * Fetches the given tab.
	 *
	 * @param tabInfo Tab to download.
	 */
	fun fetch(tabInfo: TabInfo): Song? = fetch(tabInfo.tabUrl)

	/**
	 * Fetches the given tab.
	 *
	 * @param url URL to the tab.
	 */
	@OptIn(InternalSerializationApi::class)
	fun fetch(url: String): Song? {
		// For some reason, the "meta" object sometimes comes in as an empty array
		val storePageData =
			DataFetcher.get(url, SongResult::class.serializer()) { it.replace("\"meta\":[],", "") }
		return storePageData?.store?.page?.data?.let {
			Song(it)
		}
	}
}