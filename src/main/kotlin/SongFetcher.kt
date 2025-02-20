package com.stevenfrew.ultimateguitar

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

object SongFetcher {
	@OptIn(InternalSerializationApi::class)
	fun fetch(tabInfo: TabInfo): Song? {
		// For some reason, the "meta" object sometimes comes in as an empty array
		val storePageData =
			DataFetcher.get(tabInfo.tabUrl, SongResult::class.serializer()) { it.replace("\"meta\":[],", "") }
		return storePageData?.store?.page?.data?.let {
			Song(it)
		}
	}
}