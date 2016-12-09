package io.dico.mediacatalogue.media.builder;

import io.dico.mediacatalogue.util.ConsoleOperator;
import io.dico.mediacatalogue.media.AudioTrack;
import io.dico.mediacatalogue.util.Duration;

import java.util.Map;
import java.util.function.BiConsumer;

public class AudioTrackBuilder extends AbstractMediaBuilder<AudioTrack> {

    public AudioTrackBuilder(ConsoleOperator console) {
        super(console);
    }
    
    public AudioTrackBuilder(ConsoleOperator console, Map<String, String> defaults) {
        super(console, defaults);
    }
    
    @Override
    protected void writeDefaultInputs(BiConsumer<String, String> writer) {
        writer.accept("duration", "3:30");
        writer.accept("artist", "Default Artist");
        writer.accept("record label", "Default Record Label");
    }
    
    @Override
    public Class<AudioTrack> type() {
        return AudioTrack.class;
    }

    @Override
    public AudioTrack build() {
        String title = requestTitle();
        String artist = requestField("artist");
        int releaseYear = requestReleaseYear();
        Duration duration = requestDuration(false);
        String recordLabel = requestField("record label");
        int rating = requestStarRating();
        resetSkipCount();
        return new AudioTrack(title, rating, releaseYear, duration, artist, recordLabel);
    }

}
