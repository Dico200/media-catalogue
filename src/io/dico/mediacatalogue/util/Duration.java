package io.dico.mediacatalogue.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Duration extends Number {

    private static int parseInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a number: " + input);
        }
    }

    /**
     * Attempts to parse a duration from the given input
     * A number is retrieved from the input through the following algorithm:
     * <p>
     * If the input is formatted as a number, parse it (in reality, this is try-catched).
     * And return a duration of that number * secondsEach
     * Otherwise, the string is checked for content of :
     * If it does not contain :
     * Apply the pattern -?[0-9]+[hm] if parseMinutes else -?[0-9]+[ms]
     * An example is 1h30m or 3m30s
     * Really just any number followed by the two characters at the end, 1 or more times.
     * count = 0
     * If the number is followed by the first of the two letters in the regex, add it * 60 to the count
     * If the number is followed by the other letter, add it * 1 to the count
     * If parseMinutes
     * count *= 60
     * return a duration of count seconds
     * If it does contain :
     * split the input at :
     * if the length is 2
     * try to parse a number from both sides
     * the left side is parsed as hours, and the right side as minutes
     *
     * @param input
     * @param parseMinutes
     * @return
     */
    public static Duration fromString(String input, boolean parseMinutes) {
        try {
            return new Duration(parseInt(input));
        } catch (IllegalArgumentException e) {
            if (!input.contains(":")) {
                Pattern pattern = Pattern.compile("-?[0-9]+[hm]");
                Matcher matcher = pattern.matcher(input);
                int minutes = 0;
                int end = 0;
                while (matcher.find()) {
                    int start = matcher.start();
                    if (start != end) {
                        throw new IllegalArgumentException("Invalid duration");
                    }
                    end = matcher.end();

                    String fieldInput = input.substring(start, end);
                    int count = parseInt(fieldInput.substring(0, fieldInput.length() - 1));
                    char type = fieldInput.charAt(fieldInput.length() - 1);
                    if (type == 'h') {
                        count *= 60;
                    }
                    minutes += count;
                }

                if (end != input.length()) {
                    throw new IllegalArgumentException("Invalid duration");
                }
                return new Duration(minutes);
            }
            String[] split = input.split(":");
            if (split.length == 2) {
                int hours = parseInt(split[0]);
                int minutes = parseInt(split[1]);
                minutes += hours * 60;
                return new Duration(minutes);
            }
            throw new IllegalArgumentException("Invalid duration");
        }
    }

    private final boolean minutes;
    private final int count;

    public Duration(boolean minutes, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("duration must be positive: " + count);
        }
        this.count = count;
        this.minutes = minutes;
    }

    @Override
    public int intValue() {
        return count;
    }

    @Override
    public long longValue() {
        return count;
    }

    @Override
    public float floatValue() {
        return count;
    }

    @Override
    public double doubleValue() {
        return count;
    }

    @Override
    public String toString() {
        int higher = count / 60;
        return Integer.toString(higher) + ":" + Integer.toString(count % 60);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Duration duration = (Duration) o;

        return count == duration.count;
    }

    @Override
    public int hashCode() {
        return count;
    }

}
