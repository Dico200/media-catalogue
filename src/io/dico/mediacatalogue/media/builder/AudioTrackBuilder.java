package io.dico.mediacatalogue.media.builder;

import io.dico.mediacatalogue.ConsoleOperator;
import io.dico.mediacatalogue.media.AudioTrack;
import io.dico.mediacatalogue.util.Duration;

public class AudioTrackBuilder extends AbstractMediaBuilder<AudioTrack> {

    public AudioTrackBuilder(ConsoleOperator console) {
        super(console);
        defaults.put("duration", "3");
        defaults.put("artist", "Default Artist");
        defaults.put("record label", "Default Record Label");
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
        Duration duration = requestDuration();
        String recordLabel = requestField("record label");
        int rating = requestStarRating();
        resetSkipCount();
        return new AudioTrack(title, rating, releaseYear, duration, artist, recordLabel);
    }

}
