package io.dico.mediacatalogue;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.dico.mediacatalogue.media.AudioTrack;
import io.dico.mediacatalogue.media.Film;
import io.dico.mediacatalogue.media.Media;
import io.dico.mediacatalogue.media.TelevisionProgramme;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MediaContainer extends LinkedHashSet<Media> {

    public List<? extends Media> getItemsByType(Class<? extends Media> type) {
        if (type == null) {
            return new ArrayList<>(this);
        }
        return stream().filter(type::isInstance).map(type::cast).collect(Collectors.toList());
    }

    public void load(String fileName) throws IOException {
        File file = new File(fileName);
        Reader fileReader = new FileReader(file);
        JsonReader jsonReader = new JsonReader(fileReader);
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            final String key = jsonReader.nextName();
            Supplier<Media> constructor;
            switch (key) {
                case "films":
                    constructor = Film::new;
                    break;
                case "audio tracks":
                    constructor = AudioTrack::new;
                    break;
                case "tv programmes":
                    constructor = TelevisionProgramme::new;
                    break;
                default:
                    jsonReader.skipValue();
                    continue;
            }

            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                Media media = constructor.get();
                media.readFrom(jsonReader);
                add(media);
            }
            jsonReader.endArray();
        }
        jsonReader.endObject();
        jsonReader.close();
    }

    public void save(String path, boolean createFile) throws IOException {
        File file = new File(path);
        if (createFile) {
            File parent = file.getParentFile();
            if ((parent == null || parent.exists() || parent.mkdirs()) && (file.exists() || file.createNewFile())) {
                // created file successfully
            }
        }
        PrintWriter printWriter = new PrintWriter(file);
        JsonWriter jsonWriter = new JsonWriter(printWriter);
        jsonWriter.setIndent("  ");
        jsonWriter.beginObject();
        saveMediaType("films", Film.class, jsonWriter);
        saveMediaType("audio tracks", AudioTrack.class, jsonWriter);
        saveMediaType("tv programmes", TelevisionProgramme.class, jsonWriter);
        jsonWriter.endObject();
        jsonWriter.close();
    }

    private void saveMediaType(String name, Class<? extends Media> type, JsonWriter writer) throws IOException {
        writer.name(name);
        writer.beginArray();
        for (Media media : this) {
            if (type.isInstance(media)) {
                media.writeTo(writer);
            }
        }
        writer.endArray();
    }

}
