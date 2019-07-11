package align_parsing

import com.google.gson.Gson
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.io.LineNumberReader
import java.util.*
import kotlin.Comparator

internal object AlignFileParser {
    @Throws(FileNotFoundException::class)
    fun parseFile(alignFilePath: String, textFilePath: String): Map<Int, Map<Int, ParsedAyah>> {
        val gson = Gson()
        val surahs = HashMap<Int, HashMap<Int, ParsedAyah>>()
        val lineNumberReader = LineNumberReader(FileReader(textFilePath))
        var entries = listOf(*gson.fromJson(FileReader(alignFilePath), Array<SurahEntry>::class.java))
        // Sort the file
        val surahEntryComparator = Comparator<SurahEntry> { x, y ->
            val equalSurah = x.surah == y.surah
            if (equalSurah) x.ayah.compareTo(y.ayah) else x.surah.compareTo(y.surah)
        }
        entries = entries.sortedWith(surahEntryComparator)

        for (entry in entries) {
            val segments = Segment.makeSegmentsFromIntArray(entry.segments)
            var ayah: ParsedAyah? = null
            try {
                ayah = ParsedAyah(entry.surah, entry.ayah, segments, entry.stats, lineNumberReader.readLine())
            } catch (e: IOException) {
                e.printStackTrace()
                Runtime.getRuntime().exit(-1)
            }

            if (!surahs.containsKey(entry.surah)) {
                surahs[entry.surah] = hashMapOf()
            }

            if (surahs[entry.surah]!!.containsKey(entry.ayah)) {
                throw IllegalStateException("Each Ayah has only ONE align entry")
            }

            surahs[entry.surah]!![entry.ayah] = ayah!!
        }

        return surahs
    }
}
