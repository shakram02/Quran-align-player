package quran_align_player.parsing;

import java.util.List;

public class ParsedAyah {
    private int number;
    private List<Segment> segments;

    ParsedAyah(int number, List<Segment> segments) {
        this.number = number;
        this.segments = segments;
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
}
