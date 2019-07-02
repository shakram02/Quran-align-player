package quran_align_player


import align_parsing.AnnotationResolver
import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
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

            val resolver = AnnotationResolver(alignFilePath, quranTextFilePath, annotationFilePath)

            saveStringIterable(
                resolver.extractUnresolvableAnnotations(),
                File("${recitationId}_unresolvable_annotations.txt")
            )

            saveStringIterable(
                resolver.extractResolvableAnnotations(),
                File("${recitationId}_auto_resolved_annotations.txt")
            )

            Platform.exit()
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
