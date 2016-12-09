package io.dico.mediacatalogue.media.builder;

import io.dico.mediacatalogue.media.Media;
import io.dico.mediacatalogue.util.ConsoleOperator;
import io.dico.mediacatalogue.util.Duration;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Calendar.YEAR;

public abstract class AbstractMediaBuilder<T extends Media> implements MediaBuilder<T> {

    protected static final int CURRENT_YEAR = new GregorianCalendar().get(YEAR);

    protected final ConsoleOperator console;
    protected Map<String, String> defaults;
    private int skipCount = 0;

    public AbstractMediaBuilder(ConsoleOperator console) {
        this.console = console;
        defaults = new HashMap<>();
        defaults.put("title", "Default Title");
        defaults.put("year of release", Integer.toString(CURRENT_YEAR));
        defaults.put("rating", "7");
        writeDefaultInputs(defaults::put);
    }
    
    public AbstractMediaBuilder(ConsoleOperator console, Map<String, String> defaults) {
        this.console = console;
        this.defaults = defaults;
    }
    
    protected abstract void writeDefaultInputs(BiConsumer<String, String> writer);

    protected void resetSkipCount() {
        skipCount = 0;
    }

    /**
     * Requests a value for the field from the console
     * The user can skip the value (using a default value) by writing skip
     * They can skip multiple of these requests by writing skip \<amount to skip\>
     * They can skip all future requests, up to a maximum of Integer.MAX_VALUE, by writing skip all
     * @param fieldName the field
     * @return The input, or the default value if skipped
     */
    protected String requestField(String fieldName) {
        String defaultValue = defaults.get(fieldName);
        if (skipCount > 0) {
            skipCount--;
            return defaultValue;
        }
        
        String result = console.requestLine("Enter " + fieldName, null, defaultValue);

        if (result.startsWith("skip ")) {
            String[] splitted = result.split(" ");
            if (splitted.length == 2) {
                String second = splitted[1];
                if (second.equals("all")) {
                    skipCount = Integer.MAX_VALUE;
                } else {
                    int number;
                    try {
                        number = Integer.parseInt(second);
                    } catch (NumberFormatException e) {
                        console.writeLine("You can skip 'all' or any positive number");
                        return requestField(fieldName);
                    }
                    if (number <= 0) {
                        console.writeLine("The skip count must be positive");
                        return requestField(fieldName);
                    }

                    skipCount = number;
                }
                return requestField(fieldName);
            }
        }

        return result;
    }

    /**
     * Parses an integer from input using Integer.parseInt
     * @param input the input
     * @return an integer parsed from input
     * @throws IllegalArgumentException if Integer.parseInt threw NumberFormatException
     */
    protected int parseInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("That's not a number, please try again");
        }
    }

    /**
     * Requests an integer for the field
     * @param fieldName the field
     * @return A valid integer from the console
     */
    protected int requestInt(String fieldName) {
        return console.requestWithExceptions(() -> parseInt(requestField(fieldName)), null);
    }

    /**
     * @return A title from the console
     */
    protected String requestTitle() {
        return requestField("title");
    }

    /**
     * @return A valid duration from the console
     */
    protected Duration requestDuration(boolean parseMinutes) {
        return console.requestWithExceptions(() -> Duration.fromString(requestField("duration"), parseMinutes), null);
    }

    /**
     * @return A valid release year from the console
     */
    protected int requestReleaseYear() {
        return console.requestWithValidator(() -> requestInt("year of release"), input -> (1896 <= input && input <= CURRENT_YEAR),
                "The year of release must be between 1896 and this year (" + CURRENT_YEAR + ") inclusive");
    }

    /**
     * @return A valid star rating from the console
     */
    protected int requestStarRating() {
        return console.requestWithValidator(() -> requestInt("rating"), input -> (1 <= input && input <= 10),
                "A rating must be between 1 and 10 inclusive, please try again");
    }

}
