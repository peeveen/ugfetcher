package com.stevenfrew.ultimateguitar

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

class Song(data: SongResultStorePageData) {
	val tabInfo = data.tabInfo
	private val tabView = data.tabView
	private val lines = parseLines(data.tabView.wikiTab.content)

	fun toChordPro(): List<String> {
		val titleLine = "{title:${tabInfo.songName}}"
		val artistLine = "{artist:${tabInfo.artistName}}"
		val capoLine = if (tabView.meta?.capo != null && tabView.meta.capo > 0) "{capo:${tabView.meta.capo}}" else null
		val keyLine = if (tabInfo.key.isNotBlank()) "{key:${tabInfo.key}}" else null
		val ratingLine = "{rating:${Math.round(tabInfo.rating)}}"
		val creatorLine = if (tabInfo.creator?.isNotBlank() == true) "{comment:Created by @${tabInfo.creator}}" else null
		val tuningLine =
			if (tabView.meta?.tuning != null) "{comment:Tuning = ${tabView.meta.tuning.name} (${tabView.meta.tuning.value})}" else null
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

	fun toPlainText(): List<String> {
		val artistLine = "by ${tabInfo.artistName}"
		val capoLine = if (tabView.meta?.capo != null && tabView.meta.capo > 0) "CAPO ${tabView.meta.capo}" else null
		val creatorLine = if (tabInfo.creator?.isNotBlank() == true) "Created by @${tabInfo.creator}" else null
		val tuningLine =
			if (tabView.meta?.tuning != null) "Tuning = ${tabView.meta.tuning.name} (${tabView.meta.tuning.value})" else null
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

		private fun parseLines(content: String): List<Line> =
			mutableListOf<Line>().apply {
				parseMarkers(
					content,
					TAB_MARKER_START,
					TAB_MARKER_END
				) { text, start ->
					add(parseLine(text, start == 0))
				}
			}

		private fun parseLine(line: String, isTab: Boolean): Line {
			val (text, chords) = if (isTab) {
				val lines = line.split("\r\n")
				val chords = lines[0]
				val text = lines[1]
				val (_, extractedChords) = extractChords(chords)
				text to extractedChords
			} else
				extractChords(line)
			return Line(text, chords)
		}

		private fun extractChords(line: String): Pair<String, List<Chord>> {
			val chords = mutableListOf<Chord>()
			var text = ""
			parseMarkers(line, CHORD_MARKER_START, CHORD_MARKER_END) { chordText, start ->
				if (start == 0)
					chords.add(Chord(chordText, text.length + chords.sumOf { it.name.length }))
				else
					text += chordText
			}
			return text to chords
		}

		private fun parseMarkers(
			content: String,
			startMarker: String,
			endMarker: String,
			fn: (String, Int) -> Unit
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
				}.trim('\r').trim('\n')
				if (markerContent.isNotEmpty())
					fn(markerContent, startIndex)
				workingContent = workingContent.substring(endIndex)
			}
		}
	}
}
