package io.dico.mediacatalogue.media;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.function.BiConsumer;

public class TelevisionProgramme extends AbstractMedia {

    private String series;
    private String episode;
    private String studio;
    private String channel;

    public TelevisionProgramme() {
    }

    public TelevisionProgramme(String title, int rating, int releaseYear, String series, String episode, String studio, String channel) {
        super(title, rating, releaseYear);
        this.series = series;
        this.episode = episode;
        this.studio = studio;
        this.channel = channel;
        resetFieldValues();
    }

    @Override
    public String type() {
        return "tv programme";
    }

    public String series() {
        return series;
    }

    public String episode() {
        return episode;
    }

    public String studio() {
        return studio;
    }

    public String channel() {
        return channel;
    }

    @Override
    protected void setFieldValues(BiConsumer<String, Object> fieldConsumer) {
        fieldConsumer.accept("series", series);
        fieldConsumer.accept("episode", episode);
        fieldConsumer.accept("studio", studio);
        fieldConsumer.accept("channel", channel);
    }

    @Override
    protected void writeFields(JsonWriter writer) throws IOException {
        writer.name("series").value(series);
        writer.name("episode").value(episode);
        writer.name("studio").value(studio);
        writer.name("channel").value(channel);
    }

    @Override
    protected void readField(String name, JsonReader reader) throws IOException {
        switch (name) {
            case "series":
                series = reader.nextString();
                break;
            case "episode":
                episode = reader.nextString();
                break;
            case "studio":
                studio = reader.nextString();
                break;
            case "channel":
                channel = reader.nextString();
                break;
            default:
                reader.skipValue();
                break;
        }
    }
}
