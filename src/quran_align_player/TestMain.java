package quran_align_player;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import quran_align_player.parsing.AlignFileParser;
import quran_align_player.parsing.ParsedAyah;
import quran_align_player.parsing.ParsedSurah;
import quran_align_player.parsing.Segment;
import quran_align_player.sound.SurahPlayer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TestMain extends Application {
    public static void main(String[] args) throws IOException {
        Path pathToFile = Paths.get("assets/json_filename.txt");
        List<String> config = Files.readAllLines(pathToFile);
        String alignFileName = config.get(0);
        String textFileName = config.get(1);

        HashMap<Integer, ParsedSurah> surahs = AlignFileParser.parseFile(alignFileName, textFileName);
        System.out.println(surahs.size());

        ParsedSurah s = surahs.get(1);
        SurahPlayer surahPlayer = new SurahPlayer(s.getSurahNumber(), s.getAyahCount(), "assets", (n, d) -> {
            ParsedAyah ayah = s.getAyah(n);
            Segment currentSegment = ayah.getSegmentAt((int) d.toMillis());
            String[] current = Arrays.copyOfRange(ayah.getText().split(" "),
                    currentSegment.getStartWordIndex(), currentSegment.getEndWordIndex());
            System.out.println(String.join(" ", current));
        });
        surahPlayer.play();
        Platform.exit();
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
