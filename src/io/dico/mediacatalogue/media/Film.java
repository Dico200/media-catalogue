package io.dico.mediacatalogue.media;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.dico.mediacatalogue.util.Duration;

import java.io.IOException;
import java.util.function.BiConsumer;

public class Film extends AbstractMedia {

    private Duration duration;
    private String studio;
    private String director;

    public Film() {
    }

    public Film(String title, int rating, int releaseYear, Duration duration, String studio, String director) {
        super(title, rating, releaseYear);
        this.duration = duration;
        this.studio = studio;
        this.director = director;
        resetFieldValues();
    }

    @Override
    public String type() {
        return "film";
    }

    public Duration duration() {
        return duration;
    }

    public String studio() {
        return studio;
    }

    public String getDirector() {
        return director;
    }

    @Override
    protected void setFieldValues(BiConsumer<String, Object> fieldConsumer) {
        fieldConsumer.accept("duration", duration);
        fieldConsumer.accept("studio", studio);
        fieldConsumer.accept("director", director);
    }

    @Override
    protected void writeFields(JsonWriter writer) throws IOException {
        writer.name("duration").value(duration.intValue());
        writer.name("studio").value(studio);
        writer.name("director").value(director);
    }

    @Override
    protected void readField(String name, JsonReader reader) throws IOException {
        switch (name) {
            case "duration":
                duration = new Duration(reader.nextInt(), true);
                break;
            case "studio":
                studio = reader.nextString();
                break;
            case "director":
                director = reader.nextString();
                break;
            default:
                reader.skipValue();
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
    
        Film film = (Film) o;
        return duration.equals(film.duration) && studio.equals(film.studio) && director.equals(film.director);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + duration.intValue();
        result = 31 * result + studio.hashCode();
        result = 31 * result + director.hashCode();
        return result;
    }
}
