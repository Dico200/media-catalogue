package io.dico.mediacatalogue.media;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AbstractMedia implements Media {

    private Map<String, Object> fieldValues;
    private String title;
    private int rating;
    private int releaseYear;

    public AbstractMedia() {
    }

    public AbstractMedia(String title, int rating, int releaseYear) {
        this();
        this.title = title;
        this.rating = rating;
        this.releaseYear = releaseYear;
        resetFieldValues();
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public int rating() {
        return rating;
    }

    @Override
    public int releaseYear() {
        return releaseYear;
    }

    protected abstract void setFieldValues(BiConsumer<String, Object> fieldConsumer);

    private void resetFieldValues() {
        fieldValues = new LinkedHashMap<>();
        fieldValues.put("title", title);
        fieldValues.put("year of release", releaseYear);
        setFieldValues(fieldValues::put);
        fieldValues.put("rating", rating);
        fieldValues = Collections.unmodifiableMap(fieldValues);
    }

    @Override
    public Map<String, Object> getFields() {
        return fieldValues;
    }

    protected abstract void writeFields(JsonWriter writer) throws IOException;

    protected abstract void readField(String name, JsonReader reader) throws IOException;

    @Override
    public final void writeTo(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("title").value(title);
        writer.name("rating").value(rating);
        writer.name("year of release").value(releaseYear);
        writeFields(writer);
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
                case "rating":
                    rating = reader.nextInt();
                    break;
                case "year of release":
                    releaseYear = reader.nextInt();
                    break;
                default:
                    readField(key, reader);
                    break;
            }
        }
        reader.endObject();
        resetFieldValues();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append(type());
        sb.append(')').append(' ');
        boolean first = true;
        for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append(": \"").append(entry.getValue()).append('"');
        }
        return sb.toString();
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
