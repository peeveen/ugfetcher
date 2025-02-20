import com.stevenfrew.ultimateguitar.ChordSearcher
import com.stevenfrew.ultimateguitar.SongFetcher
import kotlin.test.Test
import kotlin.test.assertNotNull

class Tests {
	@Test
	fun testSearchAndFetch1() {
		val results = ChordSearcher.search("this Used to be")
		val result =
			results.firstOrNull { it.songName == "This Used To Be My Playground" && it.artistName == "Madonna" && it.type == "Chords" }
		assertNotNull(result)
		val song = SongFetcher.fetch(result)
		assertNotNull(song)
		val chordPro = song.toChordPro().fold("") { a, v -> a + "\n" + v }
		assertNotNull(chordPro)
	}

	@Test
	fun testSearchAndFetch2() {
		val results = ChordSearcher.search("stansfield world")
		val result =
			results.lastOrNull { it.songName == "All Around The World" && it.artistName == "Lisa Stansfield" && it.type == "Chords" }
		assertNotNull(result)
		val song = SongFetcher.fetch(result)
		assertNotNull(song)
		val chordPro = song.toChordPro().fold("") { a, v -> a + "\n" + v }
		assertNotNull(chordPro)
	}
}