package io.dico.mediacatalogue.media;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class AudioTrack extends AbstractMedia {

    private int duration;
    private String artist;
    private String recordLabel;

    public AudioTrack() {
    }

    public AudioTrack(String title, int rating, int releaseYear, int duration, String artist, String recordLabel) {
        super(title, rating, releaseYear);
        this.duration = duration;
        this.artist = artist;
        this.recordLabel = recordLabel;
    }

    @Override
    public String type() {
        return "audio track";
    }

    public int duration() {
        return duration;
    }

    public String artist() {
        return artist;
    }

    public String recordLabel() {
        return recordLabel;
    }

    @Override
    protected void writeFields(JsonWriter writer) throws IOException {
        writer.name("duration").value(duration);
        writer.name("artist").value(artist);
        writer.name("recordLabel").value(recordLabel);
    }

    @Override
    protected void readFields(JsonReader reader) throws IOException {
        while (reader.hasNext()) {
            final String key = reader.nextName();
            switch (key) {
                case "duration":
                    duration = reader.nextInt();
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
}
