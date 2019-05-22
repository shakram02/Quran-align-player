package quran_align_player.parsing;

import java.util.HashMap;
import java.util.Iterator;

public class ParsedSurah {
    int surahNumber;
    private HashMap<Integer, ParsedAyah> ayahs = new HashMap<>();

    public ParsedSurah(int surahNumber) {
        this.surahNumber = surahNumber;
    }

    public void addAyah(ParsedAyah ayah) {
        ayahs.put(ayah.number, ayah);
    }

    public ParsedAyah getAyah(int ayahNum) {
        return ayahs.get(ayahNum);
    }

    public Iterator<Integer> getAyahIterator() {
        return ayahs.keySet().stream().sorted().iterator();
    }
}
