package com.github.peeveen.ultimateguitar

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

object SongFetcher {
	fun fetch(tabInfo: TabInfo): Song? = fetch(tabInfo.tabUrl)

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