package quran_align_player;

import java.time.Duration;

@FunctionalInterface
public interface AyahSoundTicker {
    void accept(int ayahNumber, Duration currentTime);
}
