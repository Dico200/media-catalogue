package io.dico.mediacatalogue.media.builder;

import io.dico.mediacatalogue.ConsoleOperator;
import io.dico.mediacatalogue.media.Media;
import io.dico.mediacatalogue.util.function.UnsafeSupplier;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Calendar.YEAR;

public abstract class AbstractMediaBuilder<T extends Media> implements MediaBuilder<T> {

    protected static final int CURRENT_YEAR = new GregorianCalendar().get(YEAR);

    private final ConsoleOperator console;
    protected final Map<String, String> defaults;
    private int skipCount = 0;

    public AbstractMediaBuilder(ConsoleOperator console) {
        this.console = console;
        defaults = new HashMap<>();
        defaults.put("title", "Default Title");
        defaults.put("year of release", Integer.toString(CURRENT_YEAR));
        defaults.put("rating", "7");
    }

    protected void resetSkipCount() {
        skipCount = 0;
    }

    protected String requestLine() {
        return console.requestLine();
    }

    protected void writeLine(String line) {
        console.writeLine(line);
    }

    protected String requestField(String fieldName) {
        if (skipCount > 0) {
            skipCount--;
            return defaults.get(fieldName);
        }

        writeLine("Enter " + fieldName);
        String result = requestLine();

        if (result.equals("skip")) {
            return defaults.get(fieldName);
        }

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
                        writeLine("You can skip 'all' or any positive number");
                        return requestField(fieldName);
                    }
                    if (number <= 0) {
                        writeLine("The skip count must be positive");
                        return requestField(fieldName);
                    }

                    skipCount = number - 1;
                    return defaults.get(fieldName);
                }
            }
        }

        return result;
    }

    protected int parseInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("That's not a number, please try again");
        }
    }

    protected int requestInt(String fieldName) {
        return requestValueWithExceptions(() -> parseInt(requestField(fieldName)));
    }

    protected <R> R requestValueWithValidation(Supplier<R> supplier, Predicate<R> validator, String ifInvalid) {
        R result;
        while (!validator.test(result = supplier.get())) {
            writeLine(ifInvalid);
        }
        return result;
    }

    protected <R> R requestValueWithExceptions(UnsafeSupplier<R, Throwable> supplier) {
        while (true) {
            try {
                return supplier.get();
            } catch (Throwable e) {
                writeLine(e.getMessage());
            }
        }
    }

    protected <R> R requestValueWithValidationAndExceptions(UnsafeSupplier<R, Throwable> supplier,  Predicate<R> validator, String ifInvalid) {
        return requestValueWithValidation(() -> requestValueWithExceptions(supplier), validator, ifInvalid);
    }

    protected String requestTitle() {
        return requestField("title");
    }

    protected int requestDuration() {
        return requestValueWithValidationAndExceptions(() -> parseDuration(requestField("duration")), minutes -> (minutes > 0),
                "The duration must be positive, please try again");
    }

    protected int requestReleaseYear() {
        return requestValueWithValidation(() -> requestInt("year of release"), input -> (1896 <= input && input <= CURRENT_YEAR),
                "The year of release must be between 1896 and this year inclusive");
    }

    protected int requestStarRating() {
        return requestValueWithValidation(() -> requestInt("rating"), input -> (1 <= input && input <= 10),
                "A rating must be between 1 and 10 inclusive, please try again");
    }

    private int parseDuration(String input) {
        try {
            return parseInt(input);
        } catch (IllegalArgumentException e) {
            if (!input.contains(":")) {
                Pattern pattern = Pattern.compile("-?[0-9]+[hm]");
                Matcher matcher = pattern.matcher(input);
                int minutes = 0;
                int end = 0;
                while (matcher.find()) {
                    int start = matcher.start();
                    if (start != end) {
                        throw new IllegalArgumentException("Invalid duration, please try again");
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
                    throw new IllegalArgumentException("Invalid duration, please try again");
                }
                return minutes;
            }
            String[] split = input.split(":");
            if (split.length == 2) {
                int hours = parseInt(split[0]);
                int minutes = parseInt(split[1]);
                minutes += hours * 60;
                return minutes;
            }
            throw new IllegalArgumentException("Invalid duration, please try again");
        }
    }

}
