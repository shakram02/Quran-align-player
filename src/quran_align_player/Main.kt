package quran_align_player


import align_generator.*
import com.google.gson.Gson
import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import quran_annotations.SurahAnnotationMap
import quran_annotations.TimestampedAnnotationFileParser
import quran_annotations.UnresolvedAnnotationFileParser
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
            // TODO: docs
            print("Enter recitation ID [Husary_128kbps]:")
            val startSurahNum = 1
            val recitationId: String = readLine() ?: "Husary_128kbps"
            val configPath = Paths.get("assets/json_filename.txt")
            System.err.println("Generating information for [$recitationId]")
            val config = Files.readAllLines(configPath)
            val quranTextFilePath = config[0]
            val fullUnresolvedQuranAnnotationFilePath = config[1]
            val alignFilePath = config[2].replace("{}", recitationId)
            val resolvedAnnotationsFilePath = config[3].replace("{}", recitationId)
            val quranAyahDurationFile = config[4].replace("{}", recitationId)
            val annotationLines = File(fullUnresolvedQuranAnnotationFilePath).readAsCleanStringList()
            val quranAnnotationEntries =
                UnresolvedAnnotationFileParser().parseAnnotationLines(annotationLines)
                    .filter { it.key >= startSurahNum }
            val quranLines = File(quranTextFilePath).readAsCleanStringList()
            val quranAlign = listOf(*Gson().fromJson(FileReader(alignFilePath), Array<SurahEntry>::class.java))
            val parsedAlignFile = AlignFileParser.parseFile(quranAlign, quranLines)
            val ayahAudioDurationInfo = AyahAudioDurationInfo(File(quranAyahDurationFile).readAsCleanStringList())

            if (recitationId == "Husary_Muallim_128kbps") {
                println("Select mode: [1]\n1) Resolve annotations\n2)Extract word by word file")
                val mode = readLine()
                if (mode == "2") {
                    extractWordByWordFile(
                        parsedAlignFile,
                        resolvedAnnotationsFilePath,
                        ayahAudioDurationInfo,
                        recitationId
                    )
                } else {
                    resolveAnnotations(
                        parsedAlignFile,
                        quranAnnotationEntries,
                        ayahAudioDurationInfo,
                        recitationId
                    )
                }
            } else {
                resolveAnnotations(
                    parsedAlignFile,
                    quranAnnotationEntries,
                    ayahAudioDurationInfo,
                    recitationId
                )
            }
            Platform.exit()
        }

        private fun resolveAnnotations(
            parsedAlignFile: ParsedAlignEntries,
            annotationEntries: Map<Int, SurahAnnotationMap>,
            ayahAudioDurationInfo: AyahAudioDurationInfo,

            recitationId: String
        ) {

            val resolver =
                AnnotationResolver(
                    parsedAlignFile,
                    annotationEntries,
                    ayahAudioDurationInfo
                )
            val resolvable = resolver.autoAlignedEntries
            val unresolvableEntries = resolver.problematicEntries
            saveExtractedAnnotations(resolvable, unresolvableEntries, recitationId)
        }

        private fun extractWordByWordFile(
            alignFile: ParsedAlignEntries,
            resolvedAnnotationsFilePath: String,
            ayahAudioDurationInfo: AyahAudioDurationInfo,
            recitationId: String
        ) {
            val lines = FileReader(resolvedAnnotationsFilePath).readLines()
            val timestampedAnnotationFile = TimestampedAnnotationFileParser().parseAnnotationLines(lines)
            val surahNumberLimitStart = 78
            val wordAligner = WordAligner(surahNumberLimitStart, ayahAudioDurationInfo)
            val alignedWords = wordAligner.alignWordsWithTextEntries(timestampedAnnotationFile, alignFile)
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

        private fun File.readAsCleanStringList(): List<String> {
            return this.readLines().map { it.trim() }.filter { it.isNotBlank() }
        }
    }
}
