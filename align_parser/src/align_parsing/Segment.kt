package align_parsing

import java.util.*

internal class Segment(
    val startWordIndex: Int, val endWordIndex: Int,
    val startMillis: Int, val endMillis: Int
) {
    private var text: String = ""
    fun setText(text: String) {
        this.text = text
    }

    fun getText(): String {
        return text
    }

    companion object {

        fun makeSegmentsFromIntArray(segArr: Array<IntArray>): List<Segment> {
            val segments = ArrayList<Segment>()

            for (segment in segArr) {
                val startWordIndex = segment[0]
                val endWordIndex = segment[1]
                val startMillis = segment[2]
                val endMillis = segment[3]
                segments.add(Segment(startWordIndex, endWordIndex, startMillis, endMillis))
            }

            return segments
        }
    }

    override fun toString(): String {
        return "[$startWordIndex:$endWordIndex]\t$text"
    }
}
