package quran_align_player.parsing;

import java.util.List;

public class ParsedAyah {
    private int number;
    private final List<Segment> segments;
    private final SurahEntry.Stats stats;
    private final String text;

    ParsedAyah(int number, List<Segment> segments, SurahEntry.Stats stats, String text) {
        this.number = number;
        this.segments = segments;
        this.stats = stats;
        this.text = text;
    }

    int getNumber() {
        return number;
    }

    public Segment getSegmentAt(int millis) {
        for (Segment s : segments) {
            if (s.endMillis >= millis)
                return s;
        }

        // This current timestamp is bigger than all elements, return the largest
        return segments.get(segments.size() - 1);
    }

    public int getDeletions() {
        return stats.deletions;
    }

    public int getTranspositions() {
        return stats.transpositions;
    }

    public int getInsertions() {
        return stats.insertions;
    }

    public String getText() {
        return text;
    }
}
