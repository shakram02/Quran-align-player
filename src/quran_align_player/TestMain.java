package quran_align_player;


import quran_align_player.parsing.AlignFileParser;
import quran_align_player.parsing.ParsedSurah;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class TestMain {
    public static void main(String[] args) throws IOException {
        Path pathToFile = Paths.get("assets/json_filename.txt");
        String fileName = Files.readAllLines(pathToFile).get(0);
        HashMap<Integer, ParsedSurah> surahs = AlignFileParser.parseFile(fileName);
        System.out.println(surahs.size());
    }
}
