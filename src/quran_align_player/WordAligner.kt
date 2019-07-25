package quran_align_player

import quran_align_parser.ArabicNormalizer
import quran_align_parser.ParsedAyah
import quran_align_parser.Segment
import quran_annotations.TextEntry
import quran_annotations.TimestampedTextEntry

class WordAligner(private val surahNumberLimitStart: Int) {

    fun alignWordsWithTextEntries(
        quranLineEntries: Map<Int, Map<Int, List<TimestampedTextEntry>>>,
        quranAlignFile: Map<Int, Map<Int, ParsedAyah>>
    ) {
        val quranLines = quranLineEntries.filter { it.key  >= surahNumberLimitStart }
        val workAlignFile = quranAlignFile.filter { it.key >= surahNumberLimitStart }

        var ayahsWithDeletions = 0
        val sortedSurahKeys = workAlignFile.keys.sorted().asReversed()
        // Print surah info form last [114] one to the first [1]
        for (surahNum in sortedSurahKeys) {

            for (ayahNum in workAlignFile[surahNum]!!.keys.sorted()) {
                val ayahInfo = workAlignFile[surahNum]!![ayahNum]!!
                if (ayahInfo.deletions != 0) {
                    System.out.flush()  // Avoid racing with stdout
                    System.err.println("Ayah with deletion [$surahNum:$ayahNum]")
                    ayahsWithDeletions++
                    continue
                }

                val textEntries = quranLines[surahNum]!![ayahNum]!!
                val lineMappedWords = alignSegmentWithLine(ayahInfo.segments, textEntries.map { it.textEntry })

                for ((segmentText, lineIndex) in lineMappedWords) {
                    println("$surahNum\t$ayahNum\t$lineIndex\t$segmentText")
                }
            }
            println()
        }

        System.out.flush()  // Avoid racing with stdout
        System.err.println("Found $ayahsWithDeletions Ayahs with deletions")
    }

    private fun alignSegmentWithLine(segments: List<Segment>, textEntries: List<TextEntry>): List<Pair<String, Int>> {
        val lineMapping: MutableList<Pair<String, Int>> = mutableListOf()
        val normalizedTextEntryLines = textEntries.map { ArabicNormalizer.normalize(it.line) }.toTypedArray()

        for (segment in segments) {
            val segmentText = segment.getText()
            val normalizedSegmentText = ArabicNormalizer.normalize(segmentText)
            val lineIndex = normalizedTextEntryLines.indexOfFirst { it.contains(normalizedSegmentText) }

            // Replace original entry to avoid finding duplicates
            normalizedTextEntryLines[lineIndex] =
                normalizedTextEntryLines[lineIndex].replaceFirst(normalizedSegmentText, "")

            lineMapping.add(Pair(segmentText, lineIndex))
        }

        return lineMapping
    }
}