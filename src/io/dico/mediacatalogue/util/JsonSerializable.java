package io.dico.mediacatalogue.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public interface JsonSerializable {

    static TypeAdapter<JsonSerializable> adapterWith(Supplier<JsonSerializable> paramConstructor) {
        return new TypeAdapter<JsonSerializable>() {

            private final Supplier<JsonSerializable> constructor = paramConstructor;

            @Override
            public void write(JsonWriter writer, JsonSerializable object) throws IOException {
                object.writeTo(writer);
            }

            @Override
            public JsonSerializable read(JsonReader reader) throws IOException {
                JsonSerializable result = constructor.get();
                result.readFrom(reader);
                return result;
            }
        };
    }

    static <T> T read(JsonReader reader) throws IOException {

        switch (reader.peek()) {
            case BEGIN_OBJECT:
                Map<String, Object> object = new HashMap<>();

                reader.beginObject();
                while (reader.hasNext()) {
                    final String key = reader.nextName();
                    final Object value = read(reader);
                    object.put(key, value);
                }
                reader.endObject();

                return (T) object;
            case BEGIN_ARRAY:
                Collection<Object> collection = new ArrayList<>();

                reader.beginArray();
                while (reader.hasNext()) {
                    final Object item = read(reader);
                    collection.add(item);
                }
                reader.endArray();

                return (T) collection;
            case BOOLEAN:
                return (T) Boolean.valueOf(reader.nextBoolean());
            case STRING:
                return (T) reader.nextString();
            case NULL:
                reader.nextNull();
                return null;
            case NUMBER:
                return (T) Double.valueOf(reader.nextDouble());
            default:
                throw new IllegalStateException();
        }

    }

    static void insert(JsonWriter writer, Object value) throws IOException {

        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            writer.beginObject();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                writer.name(entry.getKey());
                insert(writer, entry.getValue());
            }
            writer.endObject();
        } else if (value instanceof Iterable) {
            Iterable<Object> it = (Iterable<Object>) value;
            writer.beginArray();
            for (Object o : it) {
                insert(writer, o);
            }
            writer.endArray();
        } else if (value instanceof Number) {
            writer.value((Number) value);
        } else if (value instanceof Boolean) {
            writer.value((Boolean) value);
        } else if (value instanceof String) {
            writer.value((String) value);
        } else {
            throw new IllegalArgumentException("value not a String, Map, Number or Boolean");
        }

    }

    void writeTo(JsonWriter writer) throws IOException;

    void readFrom(JsonReader reader) throws IOException;

}
