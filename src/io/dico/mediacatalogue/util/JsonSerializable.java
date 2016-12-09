package io.dico.mediacatalogue.util;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

// interface for objects that can write to and load from json
public interface JsonSerializable {
    
    void writeTo(JsonWriter writer) throws IOException;
    
    void readFrom(JsonReader reader) throws IOException;
    
}