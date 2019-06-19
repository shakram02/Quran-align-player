package align_parsing;

import java.util.ArrayList;
import java.util.List;

public class Segment {
    int startWordIndex;
    int endWordIndex;
    int startMillis;
    int endMillis;

    public int getEndMillis() {
        return endMillis;
    }

    public int getEndWordIndex() {
        return endWordIndex;
    }

    public int getStartWordIndex() {
        return startWordIndex;
    }

    public int getStartMillis() {
        return startMillis;
    }

    public static List<Segment> makeSegmentsFromIntArray(int[][] segArr) {
        ArrayList<Segment> segments = new ArrayList<>();

        for (int[] segment : segArr) {
            Segment s = new Segment();
            s.startWordIndex = segment[0];
            s.endWordIndex = segment[1];
            s.startMillis = segment[2];
            s.endMillis = segment[3];
            segments.add(s);
        }

        return segments;
    }
}
