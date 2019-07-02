package align_parsing


import annotation_parsing.AnnotationParser
import annotation_parsing.TextEntry
import annotation_parsing.TimeEntry
import javafx.util.Duration
import java.io.File
import java.util.*
import kotlin.collections.HashSet

class AnnotationResolver(
    alignFilePath: String, quranTextFilePath: String, annotationFilePath: String
) {
    private val parsedAlignFile: HashMap<Int, ParsedSurah> = AlignFileParser.parseFile(alignFilePath, quranTextFilePath)
    private val annotationParser = AnnotationParser(File(annotationFilePath))
    private val problematicAlignEntries = getProblematicEntries()
    private val annotationFileResult = transformAnnotationFile()
    private val detailedAnnotationMap = annotationParser.getDetailedMap()
    private val arNumRegex = Regex("\\p{N}")

    fun extractResolvableAnnotations(): List<String> {
        val resolvableKeys = annotationFileResult.subtract(problematicAlignEntries)
        val result = mutableListOf<TimeEntry>()
        for (key in resolvableKeys) {
            val (chapterNum, sectionNum) = key.split(",").map { it.toInt() }
            val surah = parsedAlignFile[chapterNum]!!
            val alignEntry = surah.getAyah(sectionNum)
            val sectionEntries = detailedAnnotationMap[chapterNum]!![sectionNum]!!

            // Check preconditions
            assertSegmentLineAlignment(alignEntry, sectionEntries)
            assertTranslationLengthEquality(alignEntry, sectionEntries)
            result.addAll(autoAlignSection(alignEntry, sectionEntries))
        }

        val entryStrings = ArrayList<String>()
        var lastChapterNumber = ""
        var lastSectionNumber = ""
        var lineNumber = 0
        for (entry in result) {
            val textEntry = entry.textEntry
            val currentSection = textEntry.sectionNumber
            val currentChapter = textEntry.chapterNumber

            if (currentChapter == lastChapterNumber && currentSection == lastSectionNumber) {
                lineNumber++
            } else {
                lastChapterNumber = currentChapter
                lastSectionNumber = currentSection
                lineNumber = 0
            }

            entryStrings.add(
                "$currentChapter\t$currentSection\t${lineNumber.toString().padStart(2, '0')}\t" +
                        "${"%.2f".format(entry.duration.toSeconds())}\t${textEntry.line}"
            )
        }

        return entryStrings
    }

    // The decent solution is to merge the problematic words in the align file
    private val unevenTranslationWhitelist = setOf("041,047")


    fun extractUnresolvableAnnotations(): List<String> {
        // Find entries that needed to be annotated and had alignment problems
        val unresolvableKeys = annotationFileResult.intersect(problematicAlignEntries)


        val result = mutableListOf<String>()
        for (key in unresolvableKeys) {
            val (chapterNum, sectionNum) = key.split(",").map { it.toInt() }
            val sectionEntries = detailedAnnotationMap[chapterNum]!![sectionNum]!!

            for ((sentenceIndex, sentence) in sectionEntries.withIndex()) {
                val stringIndex = "%02d".format(sentenceIndex)
                result.add("${sentence.chapterNumber}\t${sentence.sectionNumber}\t$stringIndex\t${sentence.line}")
            }
        }

        return result
    }

    private fun autoAlignSection(alignEntry: ParsedAyah, sectionEntries: List<TextEntry>): List<TimeEntry> {
        val result: MutableList<TimeEntry> = mutableListOf()
        val segmentIterator = alignEntry.segments.iterator()

        /**
         * Try to align the annotation line with given segments
         * The normal case is that the segments align nicely with annotation text
         * entries.
         *
         * The worst case is that the last segment matching the end of the text entry
         * has two words and thus will wrap around to the next text entry
         */
        var lastSegEnd = 0
        var lastEndIndex = 0


        for (entry in sectionEntries) {
            val withoutNumber = entry.line.replace(arNumRegex, "")
            val entryWords = withoutNumber.split(" ").filter { it.isNotEmpty() }

            while (segmentIterator.hasNext()) {
                val segment = segmentIterator.next()
                val segmentWordIndexInTextEntry = (segment.endWordIndex - 1) - lastEndIndex

                // Seek until a segment is matched with the last word in the TextEntry
                if (segmentWordIndexInTextEntry < (entryWords.size - 1)) continue
                else if (segmentWordIndexInTextEntry >= entryWords.size) {
                    throw IllegalStateException("Exceeded text entry words")
                }

                // Segments can have insertions, which lead to many words being associated with a segment,
                // the last word only is of any significance.
                val w1 = ArabicNormalizer.normalize(segment.getText().split(" ").last())
                val w2 = ArabicNormalizer.normalize(entryWords[segmentWordIndexInTextEntry])
                if (w1 != w2) {
                    System.err.println(
                        "Bad:[${entry.chapterNumber}:${entry.sectionNumber}]?\n " +
                                "[${segment.getText()}] [${entryWords[segmentWordIndexInTextEntry]}] \n" +
                                " ${entry.line}\n${alignEntry.text}"
                    )
                    readLine()  // Prompt to see if the words are unacceptable
                }

                val entryDuration = Duration.millis((segment.endMillis - lastSegEnd).toDouble())
                val timeEntry = TimeEntry(entryDuration, entry)
                result.add(timeEntry)

                lastSegEnd = segment.endMillis
                lastEndIndex = segment.endWordIndex
                break
            }
        }

        return result
    }

    private fun assertSegmentLineAlignment(alignEntry: ParsedAyah, sectionTextEntries: List<TextEntry>): Boolean {
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

    private fun assertTranslationLengthEquality(alignEntry: ParsedAyah, sectionTextEntries: List<TextEntry>): Boolean {
        val ayahWords = alignEntry.text.split(" ").filter { it.isNotEmpty() }
        val combinedSectionWords =
            sectionTextEntries
                .map { it.line.replace(arNumRegex, "") }
                .flatMap { it.split(" ").filter { part -> part.isNotEmpty() } }

        if (ayahWords.size != combinedSectionWords.size) {
            val chapterNumber = sectionTextEntries.first().chapterNumber
            val sectionNumber = sectionTextEntries.first().sectionNumber
            if ("$chapterNumber,$sectionNumber" !in unevenTranslationWhitelist) {
                throw RuntimeException(
                    "Uneven translations [$chapterNumber:$sectionNumber] \n " +
                            "$ayahWords\n$combinedSectionWords"
                )
            }
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

        for (entry in parsedAlignFile.values) {
            val chapterNumber = entry.surahNumber.toString().padStart(3, '0')

            for (i in 1 until entry.ayahCount + 1) {
                val ayah = entry.getAyah(i)
                // Deletions are the only thing that'll make problems
                if (!isProblematic(ayah)) continue
                val sectionNumber = ayah.number.toString().padStart(3, '0')
                alignFileResult.add("$chapterNumber,$sectionNumber")
            }
        }

        return alignFileResult
    }

    private fun isProblematic(ayah: ParsedAyah): Boolean {
        return ayah.deletions != 0
    }
}
