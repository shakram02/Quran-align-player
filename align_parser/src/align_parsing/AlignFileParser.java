package align_parsing;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

class AlignFileParser {
    public static HashMap<Integer, ParsedSurah> parseFile(String alignFilePath, String textFilePath) throws FileNotFoundException {
        Gson gson = new Gson();
        HashMap<Integer, ParsedSurah> surahs = new HashMap<>();
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(textFilePath));
        List<SurahEntry> entries = Arrays.asList(
                gson.fromJson(new FileReader(alignFilePath), SurahEntry[].class));
        // Sort the file
        Comparator<SurahEntry> surahEntryComparator = (x, y) -> {
            boolean equalSurah = x.surah == y.surah;
            return equalSurah ? Integer.compare(x.ayah, y.ayah) : Integer.compare(x.surah, y.surah);
        };
        entries = entries.stream().sorted(surahEntryComparator).collect(Collectors.toList());

        for (SurahEntry entry : entries) {
            List<Segment> segments = Segment.makeSegmentsFromIntArray(entry.segments);
            ParsedAyah ayah = null;
            try {
                ayah = new ParsedAyah(entry.ayah, segments, entry.stats, lineNumberReader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
                Runtime.getRuntime().exit(-1);
            }

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
