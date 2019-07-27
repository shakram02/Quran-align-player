package quran_align_player


import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import quran_align_parser.AlignFileParser
import quran_align_parser.AnnotationResolver
import quran_align_parser.MergedEntries
import quran_align_parser.MergedTimestampedEntry
import quran_annotations.ResolvedAnnotationFileParser
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths

class Main : Application() {

    /**
     * The align_parsing.main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     *
     *
     * NOTE: This method is called on the JavaFX Application Thread.
     *
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set. The primary stage will be embedded in
     * the browser if the application was launched as an applet.
     * Applications may create other stages, if needed, but they will not be
     * primary stages and will not be embedded in the browser.
     */
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {

    }

    companion object {
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val configPath = Paths.get("assets/json_filename.txt")
            val config = Files.readAllLines(configPath)
            val alignFilePath = config[0]
            val quranTextFilePath = config[1]
            val annotationFilePath = config[2]
            val fullQuranAnnotationFilePath = config[3]
            val recitationId = File(alignFilePath).nameWithoutExtension


            println("Select mode: [1]\n1) Resolve annotations\n2)Extract word by word file")
            val mode = readLine()
            if (mode == "2") {
                val wordAnnotationDirectory = File("${recitationId}_word_annotations")
                wordAnnotationDirectory.mkdir()
                extractWordByWordFile(
                    alignFilePath,
                    fullQuranAnnotationFilePath,
                    quranTextFilePath,
                    wordAnnotationDirectory,
                    recitationId
                )
            } else {
                val resolver = AnnotationResolver(alignFilePath, quranTextFilePath, annotationFilePath)
                val resolvable = resolver.autoAlignedEntries
                val unresolvableEntries = resolver.problematicEntries
                saveExtractedAnnotations(resolvable, unresolvableEntries, recitationId)
            }

            Platform.exit()
        }

        private fun extractWordByWordFile(
            alignFilePath: String,
            annotationFilePath: String,
            quranTextFilePath: String, wordAnnotationDirectory: File, recitationId: String
        ) {
            val lines = FileReader(annotationFilePath).readLines()
            val alignFile = ResolvedAnnotationFileParser().parseAnnotationLines(lines)
            val surahNumberLimitStart = 78
            val parsedAlignFile = AlignFileParser.parseFile(alignFilePath, quranTextFilePath)
            val wordAligner = WordAligner(surahNumberLimitStart)
            val alignedWords = wordAligner.alignWordsWithTextEntries(alignFile, parsedAlignFile)
            val toBeAlignedWords = wordAligner.getLinesWithDeletions()

            // Outputs files to be used by chapter_annotation_merger script
            saveStringIterable(
                alignedWords.map { it.toString() },
                File("${recitationId}_auto_resolved_word_by_word_annotations.txt")
            )

            val toBeAnnotated = mutableListOf<String>()
            for (w in toBeAlignedWords) {
                toBeAnnotated.add(w.serialize())
            }

            saveStringIterable(
                toBeAnnotated,
                File("${recitationId}_unresolvable_word_by_word_annotations.txt")
            )
        }

        private fun saveExtractedAnnotations(
            resolvable: List<MergedTimestampedEntry>,
            unresolvableEntries: MergedEntries,
            recitationId: String
        ) {

            val unresolvable =
                unresolvableEntries.flatMap { entry ->
                    entry.value.flatMap { it.value.second }
                        .map { it.serialize() }
                }
            saveStringIterable(
                unresolvable,
                File("${recitationId}_unresolvable_annotations.txt")
            )

            saveStringIterable(
                resolvable.flatMap { pair -> pair.second.map { it.serialize() } },
                File("${recitationId}_auto_resolved_annotations.txt")
            )
        }

        private fun saveStringIterable(entries: List<String>, savePath: File, append: Boolean = false): Boolean {
            val writer = OutputStreamWriter(FileOutputStream(savePath, append), Charsets.UTF_8)
            writer.write(entries.joinToString("\r\n"))

            System.err.println("Saving ${entries.size} entries to [${savePath.name}]")
            writer.flush()
            writer.close()

            return true
        }
    }
}
