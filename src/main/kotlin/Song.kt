package com.stevenfrew.ultimateguitar

data class Chord(val name: String, val position: Int)

data class Line(val text: String, val chords: List<Chord>)

class Song(data: SongResultStorePageData) {
	val tabInfo = data.tabInfo
	val lines = parseLines(data.tabView.wikiTab.content)

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
					chords.add(Chord(chordText, text.length))
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
					val startsWithMarker = it.startsWith(startMarker)
					val endsWithMarker = it.endsWith(endMarker)
					if (startsWithMarker && endsWithMarker)
						it.substring(startMarkerLength, it.length - endMarkerLength)
					else if (startsWithMarker)
						it.substring(startMarkerLength, it.length)
					else if (endsWithMarker)
						it.substring(0, it.length - endMarkerLength)
					else it
				}.trim('\r').trim('\n')
				if (markerContent.isNotEmpty())
					fn(markerContent, startIndex)
				workingContent = workingContent.substring(endIndex)
			}
		}
	}
}
