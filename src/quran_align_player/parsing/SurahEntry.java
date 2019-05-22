package quran_align_player.parsing;

class SurahEntry {
    class Stats {
        int deletions;
        int transpositions;
        int insertions;
    }

    int ayah;
    int surah;
    int[][] segments;
    Stats stats;
}
