package align_parsing

internal class ParsedAyah
//    private final String[] textWords;

    (
    val number: Int, val segments: List<Segment>, private val stats: SurahEntry.Stats,
    //    public String getSegmentText() {
    //
    //    }

    val text: String
)//        textWords = text.split(" ")
{

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
