package quran_align_player


import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import quran_align_player.parsing.AlignFileParser
import quran_align_player.sound.SurahPlayer
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class Main : Application() {

    /**
     * The main entry point for all JavaFX applications.
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
            val pathToFile = Paths.get("assets/json_filename.txt")
            val config = Files.readAllLines(pathToFile)
            val alignFileName = config[0]
            val textFileName = config[1]

            val surahs = AlignFileParser.parseFile(alignFileName, textFileName)
            println(surahs.size)

            for (key in surahs.keys) {
                val s = surahs[key]!!
                for (i in 1 until s.ayahCount + 1) {
                    val a = s.getAyah(i)
                    if (a.deletions == 0 && a.insertions == 0 && a.transpositions == 0) continue

                    System.err.println("[$key:$i] D:${a.deletions} | T:${a.transpositions} | I:${a.insertions}\n${a.text}\n")
                }
            }

            val s = surahs[1]!!
            val surahPlayer = SurahPlayer(s.surahNumber, s.ayahCount, "assets/001") { n, d ->
                val ayah = s.getAyah(n)
                val currentSegment = ayah.getSegmentAt(d.toMillis().toInt())
                val current = Arrays.copyOfRange(
                    ayah.text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray(),
                    currentSegment.startWordIndex, currentSegment.endWordIndex
                )
                print("\r")
                print(current.joinToString(" "))
            }
            surahPlayer.play()
            Platform.exit()
        }
    }
}
