package com.stevenfrew.ultimateguitar

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup

object DataFetcher {
	internal const val ULTIMATE_GUITAR_HOST = "https://www.ultimate-guitar.com"
	private const val JS_STORE_CLASS = "js-store"
	private const val DATA_CONTENT_ATTRIBUTE = "data-content"

	private val json = Json { ignoreUnknownKeys = true }

	fun <T> get(
		url: String,
		deserializer: DeserializationStrategy<T>,
		jsonModifier: ((String) -> String)? = null
	): T? {
		val doc = Jsoup.connect(url).get()
		val result = doc.selectFirst(".$JS_STORE_CLASS")
		return result?.let {
			val jsonContent = result.attr(DATA_CONTENT_ATTRIBUTE)
			val modifiedJsonContent = jsonModifier?.invoke(jsonContent) ?: jsonContent
			val ultimateGuitarResults = json.decodeFromString(deserializer, modifiedJsonContent)
			return ultimateGuitarResults
		}
	}
}