package quran_align_player


import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import quran_align_parser.AlignFileParser
import quran_align_parser.AnnotationResolver
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
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
            val recitationId = File(alignFilePath).nameWithoutExtension

            extractWordByWordFile(alignFilePath, quranTextFilePath, recitationId)
//            resolveRecitationAnnotations(alignFilePath, quranTextFilePath, annotationFilePath, recitationId)
            Platform.exit()
        }

        private fun extractWordByWordFile(
            alignFilePath: String,
            quranTextFilePath: String, recitationId: String
        ) {
            val parsedAlignFile = AlignFileParser.parseFile(alignFilePath, quranTextFilePath)
            var ayahsWithDeletions = 0
            // Print surah info form last [114] one to the first [1]
            for (surahNum in parsedAlignFile.keys.sorted().asReversed()) {
                if (surahNum < 78) break
                for (ayahNum in parsedAlignFile[surahNum]!!.keys.sorted()) {
                    val ayahInfo = parsedAlignFile[surahNum]!![ayahNum]!!
                    if (ayahInfo.deletions != 0) {
                        System.out.flush()  // Avoid racing with stdout
                        System.err.println("Ayah with deletion [$surahNum:$ayahNum]")
                        ayahsWithDeletions++
                        continue
                    }
                    for (segment in ayahInfo.segments) {
                        println("$surahNum\t$ayahNum\t$segment")
                    }
                }
                println()
            }

            System.out.flush()  // Avoid racing with stdout
            System.err.println("Found $ayahsWithDeletions Ayahs with deletions")
        }

        private fun resolveRecitationAnnotations(
            alignFilePath: String,
            quranTextFilePath: String,
            annotationFilePath: String,
            recitationId: String
        ) {
            val resolver = AnnotationResolver(alignFilePath, quranTextFilePath, annotationFilePath)
            val (resolvable, unresolvable) = resolver.processAnnotations()
            saveStringIterable(
                unresolvable,
                File("${recitationId}_unresolvable_annotations.txt")
            )

            saveStringIterable(
                resolvable,
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
