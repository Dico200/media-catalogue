package io.dico.mediacatalogue.mediatypes;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class Movie extends AbstractMedia {

    private String studio;
    private String director;

    public Movie() {
    }

    public Movie(String title, int duration, int rating, int releaseYear, String studio, String director) {
        super(title, duration, rating, releaseYear);
        this.studio = studio;
        this.director = director;
    }

    @Override
    public String type() {
        return "Movie";
    }

    public String studio() {
        return studio;
    }

    public String director() {
        return director;
    }

    @Override
    protected void writeFields(JsonWriter writer) throws IOException {
        writer.name("studio").value(studio);
        writer.name("director").value(director);
    }

    @Override
    protected void readFields(JsonReader reader) throws IOException {
        while (reader.hasNext()) {
            final String key = reader.nextName();
            switch (key) {
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

}
