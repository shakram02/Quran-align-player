package quran_align_player.parsing;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

public class AlignFileParser {
    public static HashMap<Integer, ParsedSurah> parseFile(String filePath) throws FileNotFoundException {
        Gson gson = new Gson();
        HashMap<Integer, ParsedSurah> surahs = new HashMap<>();
        SurahEntry[] entries =
                gson.fromJson(new FileReader(filePath), SurahEntry[].class);

        for (SurahEntry entry : entries) {
            ParsedAyah ayah = new ParsedAyah();
            ayah.number = entry.ayah;
            ayah.segments = Segment.makeSegmentsFromIntArray(entry.segments);

            if (surahs.containsKey(entry.surah)) {
                ParsedSurah surah = surahs.get(entry.surah);
                surah.addAyah(ayah);
            } else {
                ParsedSurah surah = new ParsedSurah(entry.surah);
                surah.addAyah(ayah);
                surahs.put(entry.surah, surah);
            }
        }

        return surahs;
    }
}
