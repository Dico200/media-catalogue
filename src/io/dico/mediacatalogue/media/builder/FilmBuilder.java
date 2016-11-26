package io.dico.mediacatalogue.media.builder;

import io.dico.mediacatalogue.ConsoleOperator;
import io.dico.mediacatalogue.media.Film;

public class FilmBuilder extends AbstractMediaBuilder<Film> {

    public FilmBuilder(ConsoleOperator console) {
        super(console);
        defaults.put("duration", "90");
        defaults.put("studio", "Default Studio");
        defaults.put("director", "Default Director");
    }

    @Override
    public Class<Film> type() {
        return Film.class;
    }

    @Override
    public Film build() {
        String title = requestTitle();
        int duration = requestDuration();
        int releaseYear = requestReleaseYear();
        String studio = requestField("studio");
        String director = requestField("director");
        int rating = requestStarRating();

        resetSkipCount();
        return new Film(title, rating, releaseYear, duration, studio, director);
    }

}
