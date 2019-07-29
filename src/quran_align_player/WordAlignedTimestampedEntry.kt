package quran_align_player

import align_generator.Segment


data class WordAlignedTimestampedEntry(
    val surahNumber: Int,
    val ayahNumber: Int,
    val lineNumber: Int,
    val segment: Segment
) {

    override fun toString(): String {
        return "$surahNumber\t$ayahNumber\t$lineNumber\t$segment"
    }
}