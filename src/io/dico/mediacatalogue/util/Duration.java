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
    
    private static double parseDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not floating point number: " + input);
        }
    }
    
    public static Duration fromString(String input) {
        try {
            return fromString(input, true);
        } catch (Exception ex) {
            return fromString(input, false);
        }
    }

    /**
     * Attempts to parse a duration from the given input
     * The input can be formatted in hour-minute form or minute-second form.
     * Forms can be just a number, in which case it can be floating point and represents hours or minutes,
     * or a number:number form in which case the first is either hour/minute and second is minute/second
     * or a sequence of matches to the regex -?[0-9]+[hms]
     * @param input the input
     * @param parseMinutes If true, it assumes hour-minute form.
     * @throws IllegalArgumentException if a duration could not be parsed
     */
    public static Duration fromString(String input, boolean parseMinutes) {
        try {
            double number = parseDouble(input);
            int unit = 60;
            if (parseMinutes) {
                unit *= 60;
            }
            return new Duration((int) (number * unit), parseMinutes);
        } catch (IllegalArgumentException e) {
            if (!input.contains(":")) {
                Pattern pattern = Pattern.compile("-?[0-9]+[hms]");
                Matcher matcher = pattern.matcher(input);
                int seconds = 0;
                int end = 0;
                while (matcher.find()) {
                    int start = matcher.start();
                    if (start != end) {
                        String illegalChars = null;
                        if (start > end) {
                            // end is the end of the previous match.
                            illegalChars = input.substring(end, start);
                        }
                        throw new IllegalArgumentException("Characters within the input were not recognized" + (illegalChars != null ? illegalChars : ""));
                    }
                    end = matcher.end();

                    String fieldInput = input.substring(start, end);
                    int count = parseInt(fieldInput.substring(0, fieldInput.length() - 1));
                    char type = fieldInput.charAt(fieldInput.length() - 1);
                    if (type == 'h') {
                        seconds *= 3600;
                    } else if (type == 'm') {
                        seconds *= 60;
                    }
                    seconds += count;
                }

                if (end != input.length()) {
                    throw new IllegalArgumentException("Trailing characters are not recognized: " + input.substring(end, input.length()));
                }
                return new Duration(seconds, parseMinutes);
            }
            
            String[] split = input.split(":");
            if (split.length == 2) {
                int hours = parseInt(split[0]); // or minutes
                int minutes = parseInt(split[1]); // or seconds
                minutes += hours * 60;
                if (parseMinutes) {
                    minutes *= 60;
                }
                return new Duration(minutes, parseMinutes);
            }
            
            String excMsg;
            if (parseMinutes) {
                excMsg = "Duration not of the form <hours>h<minutes>m or <hours>:<minutes>, and " + e.getMessage();
            } else {
                excMsg = "Duration not of the form <minutes>m<seconds<s> or <minutes>:<seconds> and " + e.getMessage();
            }
            throw new IllegalArgumentException(excMsg);
        }
    }
    
    private final int seconds;
    private final boolean parsedMinutes;
    
    public Duration(int seconds, boolean parsedMinutes) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("duration must be positive: " + seconds);
        }
        this.seconds = seconds;
        this.parsedMinutes = parsedMinutes;
    }

    @Override
    public int intValue() {
        return seconds;
    }

    @Override
    public long longValue() {
        return seconds;
    }

    @Override
    public float floatValue() {
        return seconds;
    }

    @Override
    public double doubleValue() {
        return seconds;
    }

    @Override
    public String toString() {
        int count = seconds;
        if (parsedMinutes) {
            count /= 60;
        }
        return Integer.toString(count / 60) + ":" + Integer.toString(count % 60);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Duration duration = (Duration) o;

        return seconds == duration.seconds;
    }

    @Override
    public int hashCode() {
        return seconds;
    }

}
