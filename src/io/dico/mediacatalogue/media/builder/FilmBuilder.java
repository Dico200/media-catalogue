package io.dico.mediacatalogue.media.builder;

import io.dico.mediacatalogue.util.ConsoleOperator;
import io.dico.mediacatalogue.media.Film;
import io.dico.mediacatalogue.util.Duration;

import java.util.Map;
import java.util.function.BiConsumer;

public class FilmBuilder extends AbstractMediaBuilder<Film> {

    public FilmBuilder(ConsoleOperator console) {
        super(console);
    }
    
    public FilmBuilder(ConsoleOperator console, Map<String, String> defaults) {
        super(console, defaults);
    }
    
    @Override
    protected void writeDefaultInputs(BiConsumer<String, String> writer) {
        writer.accept("duration", "1:30");
        writer.accept("studio", "Default Studio");
        writer.accept("director", "Default Director");
    }
    
    @Override
    public Class<Film> type() {
        return Film.class;
    }

    @Override
    public Film build() {
        String title = requestTitle();
        Duration duration = requestDuration(true);
        int releaseYear = requestReleaseYear();
        String studio = requestField("studio");
        String director = requestField("director");
        int rating = requestStarRating();

        resetSkipCount();
        return new Film(title, rating, releaseYear, duration, studio, director);
    }

}
