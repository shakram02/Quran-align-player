package parsing;

class SurahEntry {
    class Stats {
        int deletions;
        int transpositions;
        int insertions;

        @Override
        public String toString() {
            return String.format("D:%d T:%d I:%d", deletions, transpositions, insertions);
        }
    }

    int ayah;
    int surah;
    int[][] segments;
    Stats stats;
}
