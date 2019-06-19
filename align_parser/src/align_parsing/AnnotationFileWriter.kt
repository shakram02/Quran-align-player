package align_parsing

import annotation_parsing.TextEntry
import java.io.File
import java.io.PrintWriter

class AnnotationFileWriter(fileName: String) {
    private val outputWriter: PrintWriter = File(fileName).printWriter()
    fun writeAnnotations(annotations: HashMap<Int, List<TextEntry>>) {
        outputWriter.use { writer ->
            for (chapterNumber in annotations.keys.sorted()) {
                val chapterItems = annotations[chapterNumber]!!
                val splitSections = splitSections(chapterItems)

                writer.print(splitSections.joinToString(separator = "") { makeSectionEntry(it) })
            }
        }
    }

    private fun makeSectionEntry(sectionEntries: List<TextEntry>): String {
        val first = sectionEntries[0]
        val headLine = "${first.chapterNumber} ${first.sectionNumber} ${first.line}\n"
        return headLine + sectionEntries.takeLast(sectionEntries.size - 1).joinToString("\n") + "\n\n\n"
    }

    private fun splitSections(entries: List<TextEntry>): List<List<TextEntry>> {
        val result = mutableListOf<List<TextEntry>>()
        var sectionNum = ""
        var chapterNum = ""
        var currentList = mutableListOf<TextEntry>()
        for (item in entries) {
            if (item.chapterNumber != chapterNum || item.sectionNumber != sectionNum) {
                if (currentList.size > 0) {
                    result.add(currentList)
                }

                sectionNum = item.sectionNumber
                chapterNum = item.chapterNumber
                currentList = mutableListOf()
            }

            currentList.add(item)
        }

        return result
    }
}