package io.dico.mediacatalogue.media.builder;

import io.dico.mediacatalogue.util.ConsoleOperator;
import io.dico.mediacatalogue.media.TelevisionProgramme;

import java.util.Map;
import java.util.function.BiConsumer;

public class TelevisionProgrammeBuilder extends AbstractMediaBuilder<TelevisionProgramme> {

    public TelevisionProgrammeBuilder(ConsoleOperator console) {
        super(console);
    }
    
    public TelevisionProgrammeBuilder(ConsoleOperator console, Map<String, String> defaults) {
        super(console, defaults);
    }
    
    @Override
    protected void writeDefaultInputs(BiConsumer<String, String> writer) {
        writer.accept("series", "Default Series");
        writer.accept("episode", "Default Episode");
        writer.accept("studio", "Default Studio");
        writer.accept("channel", "Default Channel");
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
