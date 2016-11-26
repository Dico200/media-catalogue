package io.dico.mediacatalogue.media.builder;

import io.dico.mediacatalogue.ConsoleOperator;
import io.dico.mediacatalogue.media.TelevisionProgramme;

public class TelevisionProgrammeBuilder extends AbstractMediaBuilder<TelevisionProgramme> {

    public TelevisionProgrammeBuilder(ConsoleOperator console) {
        super(console);
        defaults.put("series", "Default Series");
        defaults.put("episode", "Default Episode");
        defaults.put("studio", "Default Studio");
        defaults.put("channel", "Default Channel");
    }

    @Override
    public Class<TelevisionProgramme> type() {
        return TelevisionProgramme.class;
    }

    @Override
    public TelevisionProgramme build() {
        String title = requestTitle();
        String series = requestField("series");
        String episode = requestField("episode");
        int releaseYear = requestReleaseYear();
        String studio = requestField("studio");
        String channel = requestField("channel");
        int rating = requestStarRating();

        resetSkipCount();
        return new TelevisionProgramme(title, rating, releaseYear, series, episode, studio, channel);
    }

}
