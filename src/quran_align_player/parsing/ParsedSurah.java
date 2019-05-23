package quran_align_player.parsing;

import java.util.HashMap;

public class ParsedSurah {
    private final int surahNumber;
    private final HashMap<Integer, ParsedAyah> ayahs = new HashMap<>();

    ParsedSurah(int surahNumber) {
        this.surahNumber = surahNumber;
    }

    void addAyah(ParsedAyah ayah) {
        ayahs.put(ayah.getNumber(), ayah);
    }

    public ParsedAyah getAyah(int ayahNum) {
        return ayahs.get(ayahNum);
    }

    public int getAyahCount() {
        return ayahs.size();
    }

    public int getSurahNumber() {
        return surahNumber;
    }
}
