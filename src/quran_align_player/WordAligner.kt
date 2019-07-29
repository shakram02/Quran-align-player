package quran_align_player

import align_generator.ArabicNormalizer
import align_generator.AyahAudioDurationInfo
import align_generator.ParsedAyah
import align_generator.Segment
import quran_annotations.TextEntry
import quran_annotations.TimestampedTextEntry

class WordAligner(
    private val surahNumberLimitStart: Int,
    private val ayahAudioDurationInfo: AyahAudioDurationInfo
) {
    private val ayahsWithDeletions: MutableList<TimestampedTextEntry> = mutableListOf()
    private val arabicNumberRegex = Regex("\\p{N}")

    fun alignWordsWithTextEntries(
        quranLineEntries: Map<Int, Map<Int, List<TimestampedTextEntry>>>,
        quranAlignFile: Map<Int, Map<Int, ParsedAyah>>
    ): List<WordAlignedTimestampedEntry> {
        val result = mutableListOf<WordAlignedTimestampedEntry>()
        val quranLines = quranLineEntries.filter { it.key >= surahNumberLimitStart }
        val workAlignFile = quranAlignFile.filter { it.key >= surahNumberLimitStart }

        // Print surah info form last [114] one to the first [1]
        for (surahNum in workAlignFile.keys.sorted()) {
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
        val normalizedTextEntryLines =
            textEntries.map {
                ArabicNormalizer.normalize(it.line.replace(arabicNumberRegex, ""))
            }.toTypedArray()
        val surahNumber = textEntries.first().surahNumber
        val ayahNumber = textEntries.first().ayahNumber

        for ((i, segment) in segments.withIndex()) {
            val segmentText = segment.getText()
            val normalizedSegmentText = ArabicNormalizer.normalize(segmentText)
            val lineIndex = normalizedTextEntryLines.indexOfFirst { it.contains(normalizedSegmentText) }

            // Replace original entry to avoid finding duplicates
            normalizedTextEntryLines[lineIndex] =
                normalizedTextEntryLines[lineIndex].replaceFirst(normalizedSegmentText, "")

            // Amend segment durations using the start and end millis
            // the align word detector will not mark silence after a word as part of the word
            // which makes problems when trying to do highlighting
            if (i < segments.size - 1) {
                val nextSegment = segments[i + 1]
                val amendedSegment =
                    Segment(
                        segment.startWordIndex, segment.endWordIndex,
                        segment.startMillis, nextSegment.startMillis
                    )
                amendedSegment.setText(segment.getText())
                lineMapping.add(Pair(amendedSegment, lineIndex))
            } else {
                // Last segment, doesn't need to be fixed
                // last word goes to end of audio

                val audioDuration = (ayahAudioDurationInfo.getAyahAudioLength(surahNumber, ayahNumber) * 1000).toInt()
                val amendedSegment = Segment(
                    segment.startWordIndex, segment.endWordIndex,
                    segment.startMillis, audioDuration
                )
                amendedSegment.setText(segment.getText())
                lineMapping.add(Pair(segment, lineIndex))
            }
        }

        return lineMapping
    }

    fun getLinesWithDeletions(): List<TextEntry> {
        // ayahsWithDeletions is already sorted
        val result = mutableListOf<TextEntry>()

        for (entry in ayahsWithDeletions) {
            // Split each entry to multiple word entries to be used by SoundAnno
            val words = entry.textEntry.line.replace(arabicNumberRegex, "")
                .split(" ").filter { it.isNotBlank() }
            val textEntry = entry.textEntry
            val splitEntries =
                words.map { TextEntry(textEntry.surahNumber, textEntry.ayahNumber, textEntry.sentenceNumber, it) }

            result.addAll(splitEntries)
        }
        return result
    }
}