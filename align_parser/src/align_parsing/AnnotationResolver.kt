package align_parsing


import annotation_parsing.AnnotationParser
import annotation_parsing.TextEntry
import annotation_parsing.TimeEntry
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

public class AnnotationResolver(
    alignFilePath: String, quranTextFilePath: String, annotationFilePath: String
) {
    private val parsedAlignFile: HashMap<Int, ParsedSurah> = AlignFileParser.parseFile(alignFilePath, quranTextFilePath)
    private val annotationParser = AnnotationParser(File(annotationFilePath))
    private val problematicAlignEntries = getProblematicEntries()
    private val annotationFileResult = transformAnnotationFile()
    private val detailedAnnotationMap = annotationParser.getDetailedMap()
    private val arNumRegex = Regex("\\p{N}")

    fun extractResolvableAnnotations() {
        val resolvableKeys = annotationFileResult.subtract(problematicAlignEntries)
        for (key in resolvableKeys) {
            val (chapterNum, sectionNum) = key.split(",").map { it.toInt() }
            val surah = parsedAlignFile[chapterNum]!!
            val alignEntry = surah.getAyah(sectionNum)
            val sectionEntries = detailedAnnotationMap[chapterNum]!![sectionNum]!!
            assert(isSegmentAligned(alignEntry, sectionEntries))

//            val autoAligned = autoAlignSection(alignEntry, sectionEntries)
        }
    }

    private fun autoAlignSection(alignEntry: ParsedAyah, sectionEntries: List<TextEntry>): List<TimeEntry> {
        val sb = StringBuilder()
        val result: List<TimeEntry> = mutableListOf()
        val segmentIterator = alignEntry.segments.iterator()

        var alignedWordCount = 0
        /**
         * Try to align the annotation line with given segments
         * The normal case is that the segments align nicely with annotation text
         * entries.
         *
         * The worst case is that the last segment matching the end of the text entry
         * has two words and thus will wrap around to the next text entry
         */
        assert(isSegmentAligned(alignEntry, sectionEntries))

        for (entry in sectionEntries) {
            val entryWords = entry.line.split(" ")
            while (segmentIterator.hasNext()) {
                val segment = segmentIterator.next()
                // Segment interval is inclusive
                val count = (segment.endWordIndex - segment.startWordIndex) + 1

            }
        }

        TODO("NOT IMPLEMENTED")
    }

    fun extractNonResolvableAnnotations(): HashMap<Int, List<TextEntry>> {
        val resultSet = annotationFileResult.intersect(problematicAlignEntries)
        System.err.println(resultSet.size)

        val annotationList = annotationParser.getAnnotationMap().values.flatten()
        val unresolvableEntries = annotationList.filter {
            val key = "${it.chapterNumber},${it.sectionNumber}"
            key in resultSet
        }

        val result = HashMap<Int, ArrayList<TextEntry>>()
        unresolvableEntries.map {
            val key = it.chapterNumber.toInt()
            if (!result.containsKey(key)) {
                result[key] = arrayListOf()
            }

            result[key]!!.add(it)
        }

        val toMap = HashMap<Int, List<TextEntry>>()
        result.map { toMap.put(it.key, it.value.toList()) }

        return toMap
    }

    private fun isSegmentAligned(alignEntry: ParsedAyah, sectionTextEntries: List<TextEntry>): Boolean {
        var lastIndex = 0
        val segments = alignEntry.segments
        for (entry in sectionTextEntries) {
            val words = entry.line.replace(arNumRegex, "").split(" ").filter { it.isNotEmpty() }
            val isLineAligned = segments.any { s ->
                s.endWordIndex == lastIndex + (words.size - 1)
            }

            if (!isLineAligned) {
                throw RuntimeException(
                    "Unaligned line detected in " +
                            "[${entry.chapterNumber}:${entry.sectionNumber}]"
                )
            }
            lastIndex += (words.size - 1)
        }

        return true
    }

    private fun transformAnnotationFile(): Set<String> {
        return annotationParser.getAnnotationMap().values.flatten().map {
            "${it.chapterNumber},${it.sectionNumber}"
        }.toSet()
    }

    private fun getProblematicEntries(): Set<String> {
        val alignFileResult = HashSet<String>()
        var probCount = 0
        for (entry in parsedAlignFile.values) {

            val chapterNumber = entry.surahNumber.toString().padStart(3, '0')

            for (i in 1 until entry.ayahCount + 1) {
                val ayah = entry.getAyah(i)
                // Deletions are the only thing that'll make problems
                if (ayah.deletions == 0) continue
                val sectionNumber = ayah.number.toString().padStart(3, '0')
                alignFileResult.add("$chapterNumber,$sectionNumber")
            }
        }

        return alignFileResult
    }
}
