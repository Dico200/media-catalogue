package io.dico.mediacatalogue.media;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class Film extends AbstractMedia {

    private int duration;
    private String studio;
    private String director;

    public Film() {
    }

    public Film(String title, int releaseYear, int rating, int duration, String studio, String director) {
        super(title, rating, releaseYear);
        this.duration = duration;
        this.studio = studio;
        this.director = director;
    }

    @Override
    public String type() {
        return "film";
    }

    public int duration() {
        return duration;
    }

    public String studio() {
        return studio;
    }

    public String director() {
        return director;
    }

    @Override
    protected void writeFields(JsonWriter writer) throws IOException {
        writer.name("duration").value(duration);
        writer.name("studio").value(studio);
        writer.name("director").value(director);
    }

    @Override
    protected void readFields(JsonReader reader) throws IOException {
        while (reader.hasNext()) {
            final String key = reader.nextName();
            switch (key) {
                case "duration":
                    duration = reader.nextInt();
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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Film film = (Film) o;

        if (duration != film.duration) return false;
        if (!studio.equals(film.studio)) return false;
        return director.equals(film.director);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + duration;
        result = 31 * result + studio.hashCode();
        result = 31 * result + director.hashCode();
        return result;
    }
}
