package com.github.peeveen.ultimateguitar

import com.github.peeveen.ultimateguitar.TabInfo.Companion.UNREGISTERED_USER
import com.github.peeveen.ultimateguitar.Tuning.Companion.STANDARD_TUNING_NAME

data class Chord(val name: String, val position: Int)

data class Line(val text: String, val chords: List<Chord>) {
	private fun insertChords(textToInsertInto: String): String {
		var workingText = textToInsertInto
		var offset = 0
		chords.sortedBy { it.position }.forEach {
			val chordText = it.name
			val workingTextLength = workingText.length
			val offsetPosition = (it.position + offset).coerceAtMost(workingTextLength)
			val workingTextLeft = workingText.substring(0, offsetPosition)
			val workingTextRight = workingText.substring(offsetPosition)
			workingText = "${workingTextLeft}[${chordText}]${workingTextRight}"
			offset += chordText.length + 2
		}
		return workingText
	}

	fun toChordPro(): String = insertChords(text)

	fun toPlainText(): List<String> =
		listOf(text, insertChords(""))
}

/**
 * A "tab" that has been fetched and parsed.
 */
class Song(data: SongResultStorePageData) {
	/**
	 * The tab info (artist, title, etc.)
	 */
	val tabInfo = data.tabInfo
	private val tabView = data.tabView
	private val lines = parseLines(data.tabView.wikiTab.content)

	/**
	 * Converts the tab to ChordPro format. The result is a collection of strings: lines of the ChordPro file.
	 */
	fun toChordPro(): List<String> {
		val titleLine = "{title:${tabInfo.songName}}"
		val artistLine = "{artist:${tabInfo.artistName}}"
		val capoLine = if (tabView.meta?.capo != null && tabView.meta.capo > 0) "{capo:${tabView.meta.capo}}" else null
		val keyLine = if (tabInfo.key.isNotBlank()) "{key:${tabInfo.key}}" else null
		val rating = Math.round(tabInfo.rating).coerceAtMost(5)
		val ratingLine = if (rating > 0) "{rating:${rating}}" else null
		val creatorLine =
			if (tabInfo.creator?.isNotBlank() == true && tabInfo.creator != UNREGISTERED_USER) "{comment:Created by @${tabInfo.creator}}" else null
		val tuningLine =
			if (tabView.meta?.tuning != null && tabView.meta.tuning.name != STANDARD_TUNING_NAME)
				"{comment:Tuning = ${tabView.meta.tuning.name} (${tabView.meta.tuning.value})}"
			else
				null
		val songLines = lines.map { it.toChordPro() }
		return listOfNotNull(
			titleLine,
			artistLine,
			capoLine,
			keyLine,
			ratingLine,
			creatorLine,
			tuningLine,
			"",
			*songLines.toTypedArray()
		)
	}

	/**
	 * Converts the tab to plain text format.
	 */
	fun toPlainText(): List<String> {
		val artistLine = "by ${tabInfo.artistName}"
		val capoLine = if (tabView.meta?.capo != null && tabView.meta.capo > 0) "CAPO ${tabView.meta.capo}" else null
		val creatorLine =
			if (tabInfo.creator?.isNotBlank() == true && tabInfo.creator != UNREGISTERED_USER) "Created by @${tabInfo.creator}" else null
		val tuningLine =
			if (tabView.meta?.tuning != null && tabView.meta.tuning.name != STANDARD_TUNING_NAME)
				"Tuning = ${tabView.meta.tuning.name} (${tabView.meta.tuning.value})"
			else
				null
		val songLines = lines.flatMap { it.toPlainText() }
		return listOfNotNull(
			tabInfo.songName,
			artistLine,
			capoLine,
			creatorLine,
			tuningLine,
			"",
			*songLines.toTypedArray()
		)
	}


	companion object {
		private const val TAB_MARKER_START = "[tab]"
		private const val TAB_MARKER_END = "[/tab]"
		private const val CHORD_MARKER_START = "[ch]"
		private const val CHORD_MARKER_END = "[/ch]"

		private fun getLines(s: String) = s.split('\n', '\r').filter { it.isNotEmpty() }

		private fun parseLines(content: String): List<Line> =
			mutableListOf<Line>().apply {
				parseMarkers(
					content,
					TAB_MARKER_START,
					TAB_MARKER_END
				) { text, isTab ->
					val textLines = getLines(text)
					addAll(parseLines(textLines, isTab))
				}
			}

		private fun parseLines(lines: List<String>, isTab: Boolean): List<Line> {
			var index = 0
			val parsedLines = mutableListOf<Line>()
			while (index < lines.count()) {
				val line = lines[index++]
				val (text, extractedChords) = extractChords(line, isTab)
				if (extractedChords.any() && (text.isBlank() || lines.count() == 2)) {
					val nextLineText = if (index < lines.count()) lines[index++] else ""
					parsedLines.add(Line(nextLineText, extractedChords))
				} else
					parsedLines.add(Line(text, extractedChords))
			}
			return parsedLines
		}

		private fun extractChords(line: String, isTab: Boolean): Pair<String, List<Chord>> {
			val chords = mutableListOf<Chord>()
			var text = ""
			parseMarkers(line, CHORD_MARKER_START, CHORD_MARKER_END) { chordText, isChord ->
				if (isChord)
					chords.add(Chord(chordText, text.length + if (isTab) chords.sumOf { it.name.length } else 0))
				else
					text += chordText
			}
			return text to chords
		}

		private fun parseMarkers(
			content: String,
			startMarker: String,
			endMarker: String,
			fn: (String, Boolean) -> Unit
		) {
			var workingContent = content
			val startMarkerLength = startMarker.length
			val endMarkerLength = endMarker.length
			while (workingContent.isNotEmpty()) {
				val startIndex = workingContent.indexOf(startMarker)
				val endIndex = if (startIndex == 0)
					workingContent.indexOf(endMarker, startIndex).let {
						if (it == -1)
							workingContent.length
						else
							it + endMarkerLength
					}
				else if (startIndex > 0)
					startIndex
				else
					workingContent.length
				val markerContent = workingContent.substring(0, endIndex).let {
					val start = if (it.startsWith(startMarker)) startMarkerLength else 0
					val end = it.length - if (it.endsWith(endMarker)) endMarkerLength else 0
					it.substring(start, end)
				}.trim('\r', '\n')
				if (markerContent.isNotEmpty())
					fn(markerContent, startIndex == 0)
				workingContent = workingContent.substring(endIndex)
			}
		}
	}
}
