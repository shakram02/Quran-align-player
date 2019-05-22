package quran_align_player

import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import java.io.File

class UiMediaPlayer(filePath: File, private val playButton: Button, private val durationLabel: Label,
                    private val onEnd: () -> Unit = {}) {
    var currentTime: Duration = Duration(0.0)
        private set
        get() = mediaPlayer.currentTimeProperty().value

    var currentFile: File = File("")
        private set

    init {
        try {
            mediaPlayer.stop()
        } catch (e: UninitializedPropertyAccessException) {
            /*skip*/
        }

        val media = Media(filePath.toURI().toString())
        currentFile = filePath
        // when opening a new media file 2 objects of MediaPlayer exist in memory,
        // the old track keeps playing until it ends. the intended behaviour is
        // that only the new track plays not both tracks. Keeping a static
        // reference of the media player allows me to stop the old instance
        // before loading the new one
        mediaPlayer = MediaPlayer(media)    // HACK the static media instance changes here
        mediaPlayer.currentTimeProperty()
                .addListener { _: ObservableValue<out Duration>,
                               _: Duration,
                               newValue: Duration ->
                    Platform.runLater { durationLabel.text = "${"%.2f".format(newValue.toSeconds())} seconds" }
                }

        mediaPlayer.onEndOfMedia = Runnable {
            Platform.runLater {
                playButton.text = "Play"
                playButton.onAction = EventHandler<ActionEvent> { this.play() }
                onEnd()
            }
        }

        Platform.runLater {
            playButton.text = "Play"
            playButton.onAction = EventHandler<ActionEvent> { this.play() }
        }
    }

    fun play() {
        if (mediaPlayer.cycleDuration.subtract(currentTime) <= Duration.millis(PAUSE_OFFSET)) {
            System.err.println("[Cancel Play] current:$currentTime cycle:${mediaPlayer.cycleDuration}")
            mediaPlayer.seek(Duration.millis(0.0))
        }
        mediaPlayer.play()

        Platform.runLater {
            playButton.text = "Pause"
            playButton.onAction = EventHandler<ActionEvent> { this.pause() }
            System.err.println("[Play] $currentTime")
        }
    }

    fun pause() {
        // Pausing stops a bit off after the click
        mediaPlayer.seek(mediaPlayer.currentTime.subtract(Duration(PAUSE_OFFSET)))
        mediaPlayer.pause()

        Platform.runLater {
            playButton.text = "Resume"
            playButton.onAction = EventHandler<ActionEvent> { this.play() }

            System.err.println("[Pause] ${mediaPlayer.currentTime}")
        }
    }

    fun seek(seekTime: Duration) {
        mediaPlayer.seek(seekTime)
        System.err.println("[Seek] ${mediaPlayer.currentTime}")
    }

    companion object {
        private const val PAUSE_OFFSET = 250.0
        private lateinit var mediaPlayer: MediaPlayer
    }
}