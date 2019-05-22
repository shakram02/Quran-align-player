package quran_align_player

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.stage.WindowEvent
import java.net.URL
import java.util.*


class Controller : Initializable, EventHandler<KeyEvent> {
    @FXML
    lateinit var playButton: Button


    @FXML
    lateinit var durationLabel: Label

    @FXML
    private lateinit var selectedItemDisplayTextArea: TextArea

    @FXML
    private lateinit var seekValueTextField: TextField

    @FXML
    private lateinit var seekFwdButton: Button

    @FXML
    private lateinit var seekBckButton: Button

    @FXML
    private lateinit var mediaControlHBox: HBox

    private lateinit var mediaPlayer: UiMediaPlayer

    @FXML
    fun openMediaFile(event: ActionEvent) {
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location
     * The location used to resolve relative paths for the root object, or
     * <tt>null</tt> if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or <tt>null</tt> if
     * the root object was not localized.
     */
    override fun initialize(location: URL?, resources: ResourceBundle?) {

    }

    /**
     * Invoked when a specific event of the type for which this handler is
     * registered happens.
     *
     * @param event the event which occurred
     */
    override fun handle(event: KeyEvent?) {
        println("${event?.character} pressed")
    }

    fun onClose(event: WindowEvent) {

    }
}
