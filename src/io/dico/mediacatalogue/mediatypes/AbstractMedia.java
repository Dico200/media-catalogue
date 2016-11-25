package io.dico.mediacatalogue.mediatypes;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public abstract class AbstractMedia implements Media {

    private String title;
    private int duration;
    private int rating;
    private int releaseYear;

    public AbstractMedia() {
    }

    public AbstractMedia(String title, int duration, int rating, int releaseYear) {
        this.title = title;
        this.duration = duration;
        this.rating = rating;
        this.releaseYear = releaseYear;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public int duration() {
        return duration;
    }

    @Override
    public int rating() {
        return rating;
    }

    @Override
    public int releaseYear() {
        return releaseYear;
    }

    protected abstract void writeFields(JsonWriter writer) throws IOException;

    protected abstract void readFields(JsonReader reader) throws IOException;

    @Override
    public final void writeTo(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("title").value(title);
        writer.name("duration").value(duration);
        writer.name("rating").value(rating);
        writer.name("releaseYear").value(releaseYear);
        writer.name("additionalFields");
        writer.beginObject();
        writeFields(writer);
        writer.endObject();
        writer.endObject();
    }

    @Override
    public final void readFrom(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            final String key = reader.nextName();
            switch (key) {
                case "title":
                    title = reader.nextString();
                    break;
                case "duration":
                    duration = reader.nextInt();
                    break;
                case "rating":
                    rating = reader.nextInt();
                    break;
                case "releaseYear":
                    releaseYear = reader.nextInt();
                    break;
                case "additionalFields":
                    reader.beginObject();
                    readFields(reader);
                    reader.endObject();
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

        AbstractMedia that = (AbstractMedia) o;

        if (releaseYear != that.releaseYear) return false;
        if (!type().equals(that.type())) return false;
        return title.equals(that.title);
    }

    @Override
    public int hashCode() {
        int result = type().hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + releaseYear;
        return result;
    }

}
