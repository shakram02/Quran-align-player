package align_parsing


import annotation_parser.SimpleAnnotationFileParser
import annotation_parser.TextEntry
import annotation_parser.TimeEntry
import java.io.File

internal typealias AyahAnnotationList = List<TextEntry>
internal typealias SurahAnnotationMap = Map<Int, AyahAnnotationList>
internal typealias MergedEntry = Pair<ParsedAyah, List<TextEntry>>
internal typealias AnnotationFile = Map<Int, SurahAnnotationMap>
internal typealias AlignFile = Map<Int, Map<Int, ParsedAyah>>
internal typealias MergedEntries = Map<Int, Map<Int, MergedEntry>>

class AnnotationResolver(
    alignFilePath: String, quranTextFilePath: String, annotationFilePath: String
) {
    private val mergedEntries: MergedEntries

    init {
        val annotationEntries = SimpleAnnotationFileParser()
            .parseAnnotationLines(File(annotationFilePath).readLines()
                .map { it.trim() }
                .filter { it.isNotBlank() })

        val parsedAlignFile =
            AlignFileParser.parseFile(alignFilePath, quranTextFilePath)
        mergedEntries = mergeAnnotationWithAlignEntries(annotationEntries, parsedAlignFile)
    }

    private val arNumRegex = Regex("\\p{N}")

    /**
     * A facade that wraps the [AnnotationResolver] functionality
     * @return A pair the contains two lists: (resolvable annotations, unresolvable annotations)
     */
    fun processAnnotations(): Pair<List<String>, List<String>> {
        val (nonProblematic, problematic) = filterProblematic()

        val resolvable = extractResolvableAnnotations(nonProblematic)
        val unResolvable = extractUnresolvableAnnotations(problematic)
        return Pair(resolvable, unResolvable)
    }

    private fun extractResolvableAnnotations(nonProblematic: MergedEntries): List<String> {
        TODO()
//        val resolvableKeys = annotationFileResult.subtract(problematicAlignEntries)
//        val result = mutableListOf<TimeEntry>()
//        for (key in resolvableKeys) {
//            val (chapterNum, sectionNum) = key.split(",").map { it.toInt() }
//            val surah = parsedAlignFile[chapterNum]!!
//            val alignEntry = surah.getAyah(sectionNum)
//            val sectionEntries = annotationMap[chapterNum]!![sectionNum]!!
//
//            // Check preconditions
//            if (!isSegmentLineAligned(alignEntry, sectionEntries)) {
//                // TODO: Add to unresolvable
//            }
//            assertTranslationLengthEquality(alignEntry, sectionEntries)
//            result.addAll(autoAlignSection(alignEntry, sectionEntries))
//        }
//
//        val entryStrings = ArrayList<String>()
//        var lastChapterNumber = ""
//        var lastSectionNumber = ""
//        var lineNumber = 0
//        for (entry in result) {
//            val textEntry = entry.textEntry
//            val currentSection = textEntry.sectionNumber
//            val currentChapter = textEntry.chapterNumber
//
//            if (currentChapter == lastChapterNumber && currentSection == lastSectionNumber) {
//                lineNumber++
//            } else {
//                lastChapterNumber = currentChapter
//                lastSectionNumber = currentSection
//                lineNumber = 0
//            }
//
//            entryStrings.add(
//                "$currentChapter\t$currentSection\t${lineNumber.toString().padStart(2, '0')}\t" +
//                        "${"%.2f".format(entry.duration.toSeconds())}\t${textEntry.line}"
//            )
//        }
//
//        return entryStrings
    }

    private fun extractUnresolvableAnnotations(problematic: MergedEntries): List<String> {
        TODO()
//        // Find entries that needed to be annotated and had alignment problems
//        val unresolvableKeys = annotationFileResult.intersect(problematicAlignEntries)
//
//        val sorted = unresolvableKeys.sortedWith(Comparator { p0, p1 ->
//            val (ch0, sc0) = p0.split(",").map { it.toInt() }
//            val (ch1, sc1) = p1.split(",").map { it.toInt() }
//
//            if (ch0.compareTo(ch1) != 0) return@Comparator ch0.compareTo(ch1)
//            return@Comparator sc0.compareTo(sc1)
//        })
//
//        val result = mutableListOf<String>()
//        for (key in sorted) {
//            val (chapterNum, sectionNum) = key.split(",").map { it.toInt() }
//            val sectionEntries = annotationMap[chapterNum]!![sectionNum]!!
//
//            for ((sentenceIndex, sentence) in sectionEntries.withIndex()) {
//                val sentenceNumber = "%02d".format(sentenceIndex)
//                result.add("${sentence.chapterNumber}\t${sentence.sectionNumber}\t$sentenceNumber\t${sentence.line}")
//            }
//        }
//
//        return result
    }

    private fun autoAlignSection(alignEntry: ParsedAyah, sectionEntries: List<TextEntry>): List<TimeEntry> {
        TODO()
//        val result: MutableList<TimeEntry> = mutableListOf()
//        val segmentIterator = alignEntry.segments.iterator()
//
//        /**
//         * Try to align the annotation line with given segments
//         * The normal case is that the segments align nicely with annotation text
//         * entries.
//         *
//         * The worst case is that the last segment matching the end of the text entry
//         * has two words and thus will wrap around to the next text entry
//         */
//        var lastSegEnd = 0
//        var lastEndIndex = 0
//
//
//        for (entry in sectionEntries) {
//            val withoutNumber = entry.line.replace(arNumRegex, "")
//            val entryWords = withoutNumber.split(" ").filter { it.isNotEmpty() }
//
//            while (segmentIterator.hasNext()) {
//                val segment = segmentIterator.next()
//                val segmentWordIndexInTextEntry = (segment.endWordIndex - 1) - lastEndIndex
//
//                // Seek until a segment is matched with the last word in the TextEntry
//                if (segmentWordIndexInTextEntry < (entryWords.size - 1)) continue
//                else if (segmentWordIndexInTextEntry >= entryWords.size) {
//                    throw IllegalStateException("Exceeded text entry words")
//                }
//
//                // Segments can have insertions, which lead to many words being associated with a segment,
//                // the last word only is of any significance.
//                val w1 = normalize(segment.getText().split(" ").last())
//                val w2 = normalize(entryWords[segmentWordIndexInTextEntry])
//                if (w1 != w2) {
//                    System.err.println(
//                        "Bad:[${entry.chapterNumber}:${entry.sectionNumber}]?\n " +
//                                "[${segment.getText()}] [${entryWords[segmentWordIndexInTextEntry]}] \n" +
//                                " ${entry.line}\n${alignEntry.text}"
//                    )
//                    readLine()  // Prompt to see if the words are unacceptable
//                }
//
                // TODO: change duration
//                val entryDuration = Duration.millis((segment.endMillis - lastSegEnd).toLong())
//                val timeEntry = TimeEntry(entryDuration, entry)
//                result.add(timeEntry)
//
//                lastSegEnd = segment.endMillis
//                lastEndIndex = segment.endWordIndex
//                break
//            }
//        }
//
//        return result
    }

    private fun filterProblematic(): Pair<MergedEntries, MergedEntries> {
        val problematicEntries = mutableMapOf<Int, MutableMap<Int, MergedEntry>>()
        val nonProblematicEntries = mutableMapOf<Int, MutableMap<Int, MergedEntry>>()

        forEachAyahItem { chNum, ayahNum, alignEntry, annotationEntries ->
            if (!isProblematic(alignEntry, annotationEntries)) {
                if (!nonProblematicEntries.containsKey(chNum)) {
                    nonProblematicEntries[chNum] = mutableMapOf()
                }

                nonProblematicEntries[chNum]!![ayahNum] = Pair(alignEntry, annotationEntries)
            } else {
                if (!problematicEntries.containsKey(chNum)) {
                    problematicEntries[chNum] = mutableMapOf()
                }


                problematicEntries[chNum]!![ayahNum] = Pair(alignEntry, annotationEntries)
            }

        }

        return Pair(nonProblematicEntries, problematicEntries)
    }

    private val normalizationCache: HashMap<String, String> = hashMapOf()

    /**
     * Checks if the last word in each [TextEntry] matches a [Segment] in the given [ParsedAyah]
     * to make sure that [autoAlignSection] will work correctly
     */
    private fun isSegmentLineAligned(alignEntry: ParsedAyah, sectionTextEntries: List<TextEntry>): Boolean {
        var offset = 0
        val segmentIterator = alignEntry.segments.reversed().iterator()
        var segmentWordShift = 0

        for (entry in sectionTextEntries.reversed()) {
            val last = entry.line.replace(arNumRegex, "").split(" ").last { it.isNotBlank() }
            val lastWord = normalize(last)

            while (segmentIterator.hasNext()) {
                val next = normalize(segmentIterator.next().getText())
                if (next == lastWord) {
                    break
                }
            }

            if (!segmentIterator.hasNext()) {
                return false
            }


//            val w1 = normalize(words.last())
//            val w2 = normalize(segments[offset + (words.size - 1)].getText())

//            val isLineAligned = (w1 == w2)
//            val isLineAligned = segments.any { s ->
//                s.endWordIndex >= lastIndex + (words.size - 1)
//            }

//            if (!isLineAligned) {
//                return false
//            }
//            offset += words.size
        }

        return true
    }

    private fun areEqualInLength(alignEntry: ParsedAyah, sectionTextEntries: List<TextEntry>): Boolean {
        val ayahWords = alignEntry.text.split(" ").filter { it.isNotEmpty() }
        val combinedSectionWords =
            sectionTextEntries
                .map { it.line.replace(arNumRegex, "")}
                .flatMap { it.split(" ").filter { part -> part.isNotEmpty() } }

        if (ayahWords.size != combinedSectionWords.size) {
            System.err.println(
                "Unequal lines:\n[${ayahWords.size}]:\t${alignEntry.text}" +
                        "\n[${combinedSectionWords.size}]:\t${combinedSectionWords.joinToString(" ")}"
            )
            return false
        }

        return true
    }

    private fun mergeAnnotationWithAlignEntries(
        parsedAnnotationFile: AnnotationFile,
        parsedAlignFile: AlignFile
    ): MergedEntries {
        val merged = mutableMapOf<Int, MutableMap<Int, Pair<ParsedAyah, List<TextEntry>>>>()
        for (surahNumber in parsedAnnotationFile.keys) {
            val surahEntry = parsedAnnotationFile[surahNumber]!!

            for (ayahNumber in surahEntry.keys) {
                val ayahEntries = surahEntry[ayahNumber]!!
                val alignEntry = parsedAlignFile[surahNumber]!![ayahNumber]!!

                if (!merged.containsKey(surahNumber)) {
                    merged[surahNumber] = mutableMapOf()
                }

                merged[surahNumber]!![ayahNumber] = Pair(alignEntry, ayahEntries)
            }
        }

        return merged
//        for (surahNumber in parsedAlignFile.keys) {
//            val surahEntries = parsedAlignFile[surahNumber]!!
//            for(ayahNumber in surahEntries.keys){
//                val ayahEntries =
//            }
//            val chapterNumber = surahNumber.surahNumber.toString().padStart(3, '0')
//
//            for (i in 1 until surahNumber.ayahCount + 1) {
//                val ayah = surahNumber.getAyah(i)
//                // Deletions are the only thing that'll make problems
//                if (!isProblematic(ayah)) continue
//                val sectionNumber = ayah.number.toString().padStart(3, '0')
//                alignFileResult.add("$chapterNumber,$sectionNumber")
//            }
//        }
//
//        return alignFileResult
    }

    private fun isProblematic(ayah: ParsedAyah, annotationEntries: List<TextEntry>): Boolean {
        val hasDeletions = ayah.deletions != 0
        val isNotAligned = !isSegmentLineAligned(ayah, annotationEntries)
        val areNotEqualLength = !areEqualInLength(ayah, annotationEntries)
        val all = hasDeletions || isNotAligned || areNotEqualLength
        return all
    }

    private fun forEachAyahItem(f: (Int, Int, ParsedAyah, List<TextEntry>) -> Unit) {
        for (surahNumber in mergedEntries.keys.sorted()) {
            val surahEntries = mergedEntries[surahNumber]!!
            for (ayahNumber in surahEntries.keys.sorted()) {
                val (alignEntry, annotationEntries) = surahEntries[ayahNumber]!!
                f(surahNumber, ayahNumber, alignEntry, annotationEntries)
            }
        }
    }

    private fun normalize(str: String): String {
        if (normalizationCache.containsKey(str)) return normalizationCache[str]!!

        val normalized = ArabicNormalizer.normalize(str)
        normalizationCache[str] = normalized
        return normalized
    }
}
