package io.dico.mediacatalogue.util;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class ObjectFieldWriter extends JsonWriter {

    private Map<String, String> map;
    private String lastName = null;

    public ObjectFieldWriter(Map<String, String> map) {
        super(new StringWriter());
        this.map = map;
    }

    @Override
    public JsonWriter name(String name) throws IOException {
        if (lastName != null) {
            throw new IOException();
        }
        lastName = name;
        return this;
    }

    @Override
    public JsonWriter value(String value) throws IOException {
        if (lastName == null) {
            throw new IOException();
        }
        map.put(lastName, value);
        lastName = null;
        return this;
    }

    @Override
    public JsonWriter value(boolean value) throws IOException {
        return value(Boolean.toString(value));
    }

    @Override
    public JsonWriter value(double value) throws IOException {
        return value(Double.toString(value));
    }

    @Override
    public JsonWriter value(long value) throws IOException {
        return value(Long.toString(value));
    }

    @Override
    public JsonWriter value(Number value) throws IOException {
        return value(value.toString());
    }

    @Override
    public JsonWriter beginObject() throws IOException {
        return this;
    }

    @Override
    public JsonWriter endObject() throws IOException {
        return this;
    }

    @Override
    public JsonWriter nullValue() throws IOException {
        return value("null");
    }

}
