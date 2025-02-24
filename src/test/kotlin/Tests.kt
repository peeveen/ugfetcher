import com.github.peeveen.ultimateguitar.ChordSearcher
import com.github.peeveen.ultimateguitar.SongFetcher
import kotlin.test.Test
import kotlin.test.assertEquals
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

	@Test
	fun testSearchWithNoResults() {
		val results = ChordSearcher.search("wepfiojkweofwepfkwepofkwep")
		assertEquals(0, results.count())
	}

	@Test
	fun testFetches() {
		val song = SongFetcher.fetch("https://tabs.ultimate-guitar.com/tab/george-michael/father-figure-chords-8343")
		assertNotNull(song)
		val chordPro = song.toChordPro().fold("") { a, v -> a + "\n" + v }
		assertNotNull(chordPro)
	}

	@Test
	fun testOddChords() {
		val song =
			SongFetcher.fetch("https://tabs.ultimate-guitar.com/tab/garth-brooks/friends-in-low-places-chords-1087784")
		assertNotNull(song)
		val chordPro = song.toChordPro().fold("") { a, v -> a + "\n" + v }
		assertNotNull(chordPro)
	}

	@Test
	fun testTwoLineAnomaly() {
		val song =
			SongFetcher.fetch("https://tabs.ultimate-guitar.com/tab/pet-shop-boys/left-to-my-own-devices-chords-2433069")
		assertNotNull(song)
		val chordPro = song.toChordPro().fold("") { a, v -> a + "\n" + v }
		assertNotNull(chordPro)
	}
}