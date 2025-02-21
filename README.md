# Ultimate Guitar Fetcher

Fetches search results and/or chords/lyrics from [Ultimate Guitar](www.ultimateguitar.coom).

## Usage

```kotlin
val searchResults = ChordSearcher.search("around world lisa stansfield")
... etc ...
val song = SongFetcher.fetch(searchResults.first())
val chordProLines = song.toChordPro()
``