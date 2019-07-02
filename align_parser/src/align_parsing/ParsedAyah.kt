package align_parsing

internal class ParsedAyah
    (
    val number: Int, val segments: List<Segment>, private val stats: SurahEntry.Stats,
    val text: String
) {

    init {
        if (stats.deletions == 0) {
            val textWords: List<String> = text.split(" ").filter { it.isNotEmpty() }
            for (segment in segments) {
                val range = segment.startWordIndex until segment.endWordIndex
                segment.setText((textWords.slice(range).joinToString(" ")))
            }
        }
    }

    val deletions: Int
        get() = stats.deletions

    val transpositions: Int
        get() = stats.transpositions

    val insertions: Int
        get() = stats.insertions

    fun getSegmentAt(millis: Int): Segment {
        for (s in segments) {
            if (s.endMillis >= millis)
                return s
        }

        // This current timestamp is bigger than all elements, return the largest
        return segments[segments.size - 1]
    }
}
