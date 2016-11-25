package io.dico.mediacatalogue;

import io.dico.mediacatalogue.mediatypes.Media;
import io.dico.mediacatalogue.mediatypes.Movie;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionalInterface
public interface MediaConstructor<T extends Media> {

    T construct(Supplier<String> consoleInput, Consumer<String> consoleOutput);

    MediaConstructor<Movie> MOVIE_CONSTRUCTOR;
/*
    static BiFunction<Supplier<String>, Consumer<String>, Map<String, String>> fieldConstructor(List<String> fields) {
        //fields = ImmutableList.of("")
        return (in, out) -> {
            Map<String, String> result = new HashMap<>();
            for (String field : fields) {
                out.accept("Enter " + field);
                result.put(field, in.get());
            }
            return result;
        };
    }
*/
    static {

        ToIntFunction<String> durationParser = input -> {
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                String[] split = input.split(":");
                if (split.length == 0) {
                    throw new IllegalArgumentException("Invalid duration, please try again");
                } else if (split.length == 1) {
                    Pattern pattern = Pattern.compile("-?[0-9]+[hm]");
                    Matcher matcher = pattern.matcher(input);
                    pattern.split()

                    while (matcher.find()) {
                        int start = matcher.start();
                        int end = matcher.end();
                    }



                }

            }


        };

        MOVIE_CONSTRUCTOR = (in, out) -> {
            out.accept("Enter title");
            String title = in.get();
            out.accept("Enter duration");
            String duration = in.get();


        };
    }

}
