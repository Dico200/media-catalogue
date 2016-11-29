package io.dico.mediacatalogue.media;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.dico.mediacatalogue.util.Duration;

import java.io.IOException;
import java.util.function.BiConsumer;

public class AudioTrack extends AbstractMedia {

    private Duration duration;
    private String artist;
    private String recordLabel;

    public AudioTrack() {
    }

    public AudioTrack(String title, int rating, int releaseYear, Duration duration, String artist, String recordLabel) {
        super(title, rating, releaseYear);
        this.duration = duration;
        this.artist = artist;
        this.recordLabel = recordLabel;
    }

    @Override
    public String type() {
        return "audio track";
    }

    public Duration duration() {
        return duration;
    }

    public String artist() {
        return artist;
    }

    public String recordLabel() {
        return recordLabel;
    }

    @Override
    protected void setFieldValues(BiConsumer<String, Object> fieldConsumer) {
        fieldConsumer.accept("duration", duration);
        fieldConsumer.accept("artist", artist);
        fieldConsumer.accept("record label", recordLabel);
    }

    @Override
    protected void writeFields(JsonWriter writer) throws IOException {
        writer.name("duration").value(duration);
        writer.name("artist").value(artist);
        writer.name("record label").value(recordLabel);
    }

    @Override
    protected void readField(String name, JsonReader reader) throws IOException {
        switch (name) {
            case "duration":
                duration = new Duration(Math.abs(reader.nextInt()));
                break;
            case "artist":
                artist = reader.nextString();
                break;
            case "recordLabel":
                recordLabel = reader.nextString();
                break;
            default:
                reader.skipValue();
                break;
        }
    }
}
