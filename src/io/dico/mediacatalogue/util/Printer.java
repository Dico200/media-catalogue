package io.dico.mediacatalogue.util;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import io.dico.mediacatalogue.media.Media;

import java.util.*;

public class Printer {
    
    public static String multiply(char character, int amount) {
        return new String(new char[amount]).replace('\0', character);
    }
    
    public static String whitespace(int length) {
        return multiply(' ', length);
    }
    
    private static String constructTableRow(int[] fieldLength, String spacer, Collection<String> values) {
        StringBuilder line = new StringBuilder();
        int index = 0;
        boolean first = true;
        for (String value : values) {
            if (first) {
                first = false;
            } else {
                line.append(spacer);
            }
            
            line.append(value);
            int targetLength = fieldLength[index];
            if (targetLength > value.length()) {
                line.append(whitespace(targetLength - value.length()));
            }
            
            index++;
        }
        return line.toString();
    }
    
    public static boolean containsOneDistinctMediaType(Collection<Media> items) {
        String type = null;
        for (Media item : items) {
            if (type == null) {
                type = item.type();
            } else if (!type.equals(item.type())) {
                return false;
            }
        }
        return true;
    }
    
    public static String createItemTable(List<Media> items, boolean displayNumber) {
        if (items.isEmpty()) {
            return null;
        }
        
        // to check if we didn't already cover the media class. We don't want to iterate all fields of all media items now.
        Set<Class<?>> covered = new HashSet<>(3);
        
        // for checking if the field is already added
        // much faster than checking ifi the list contains it.
        Set<String> fieldSet = new HashSet<>();
        
        // ordered list of fields we want to display.
        List<String> fieldsOrdered = new LinkedList<>();
        for (Media item : items) {
            if (!covered.add(item.getClass())) continue;
            // the class has not yet been covered
            
            int index = 0;
            // getFields() returns an ordered map. I want to somewhat preserve order if there are multiple classes of media.
            for (String field : item.getFields().keySet()) {
                if (fieldSet.add(field)) {
                    // fields didn't contain the field yet
                    // add the field at the current index to preserve order
                    fieldsOrdered.add(index, field);
                }
                index++;
            }
        }
        
        StringBuilder lines = new StringBuilder();
        int maxIndexLength;
        if (displayNumber) {
            maxIndexLength = Integer.toString(items.size()).length();
        } else {
            maxIndexLength = 0;
        }
    
        int[] maximumSizes = new int[fieldsOrdered.size()];
        int index = 0;
        Multimap<Media, String> values = LinkedListMultimap.create();
    
        // find the longest length entry for each field
        // populate the values map with our values
        for (String field : fieldsOrdered) {
            int maximumSize = field.length();
            for (Media item : items) {
                Object value = item.getFields().get(field);
                if (value == null) {
                    values.put(item, "-");
                    continue;
                }
                String valueString = value.toString();
                values.put(item, valueString);
                if (valueString.length() > maximumSize) {
                    maximumSize = valueString.length();
                }
            }
            maximumSizes[index] = maximumSize;
            index++;
        }
    
        // add the header
        if (displayNumber) {
            lines.append(whitespace(maxIndexLength + 2));
        }
        lines.append(constructTableRow(maximumSizes, " | ", fieldsOrdered));
    
        // add the values
        int number = 1;
        for (Collection<String> valueList : values.asMap().values()) {
            lines.append('\n');
            if (displayNumber) {
                String numberString = Integer.toString(number++);
                lines.append(numberString);
                if (maxIndexLength > numberString.length()) {
                    lines.append(whitespace(maxIndexLength - numberString.length()));
                }
                lines.append(": ");
            }
            lines.append(constructTableRow(maximumSizes, " | ", valueList));
        }
        
        return lines.toString();
    }
    
}
