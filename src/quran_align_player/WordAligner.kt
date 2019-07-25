package quran_align_player

import quran_align_parser.ArabicNormalizer
import quran_align_parser.ParsedAyah
import quran_align_parser.Segment
import quran_annotations.TextEntry
import quran_annotations.TimestampedTextEntry

class WordAligner(private val surahNumberLimitStart: Int) {
    private val ayahsWithDeletions: MutableList<TimestampedTextEntry> = mutableListOf()

    fun alignWordsWithTextEntries(
        quranLineEntries: Map<Int, Map<Int, List<TimestampedTextEntry>>>,
        quranAlignFile: Map<Int, Map<Int, ParsedAyah>>
    ): List<WordAlignedTimestampedEntry> {
        val result = mutableListOf<WordAlignedTimestampedEntry>()
        val quranLines = quranLineEntries.filter { it.key >= surahNumberLimitStart }
        val workAlignFile = quranAlignFile.filter { it.key >= surahNumberLimitStart }

        val sortedSurahKeys = workAlignFile.keys.sorted().asReversed()
        // Print surah info form last [114] one to the first [1]
        for (surahNum in sortedSurahKeys) {
            for (ayahNum in workAlignFile[surahNum]!!.keys.sorted()) {
                val ayahInfo = workAlignFile[surahNum]!![ayahNum]!!
                val textEntries = quranLines[surahNum]!![ayahNum]!!

                if (ayahInfo.deletions != 0) {
                    // TODO: include segments?
                    ayahsWithDeletions.addAll(textEntries)
                    continue
                }

                val lineMappedWords = alignSegmentWithLine(ayahInfo.segments, textEntries.map { it.textEntry })

                for ((segment, lineIndex) in lineMappedWords) {
                    result.add(WordAlignedTimestampedEntry(surahNum, ayahNum, lineIndex, segment))
                }
            }
        }
        return result
    }

    private fun alignSegmentWithLine(segments: List<Segment>, textEntries: List<TextEntry>): List<Pair<Segment, Int>> {
        val lineMapping: MutableList<Pair<Segment, Int>> = mutableListOf()
        val normalizedTextEntryLines = textEntries.map { ArabicNormalizer.normalize(it.line) }.toTypedArray()

        for (segment in segments) {
            val segmentText = segment.getText()
            val normalizedSegmentText = ArabicNormalizer.normalize(segmentText)
            val lineIndex = normalizedTextEntryLines.indexOfFirst { it.contains(normalizedSegmentText) }

            // Replace original entry to avoid finding duplicates
            normalizedTextEntryLines[lineIndex] =
                normalizedTextEntryLines[lineIndex].replaceFirst(normalizedSegmentText, "")

            lineMapping.add(Pair(segment, lineIndex))
        }

        return lineMapping
    }

    fun getLinesWithDeletions(): List<TimestampedTextEntry> {
        return ayahsWithDeletions
    }
}