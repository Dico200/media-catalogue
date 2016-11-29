package io.dico.mediacatalogue.util;

public class FieldValue {

    private final String name;
    private final Object value;

    public FieldValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

}
