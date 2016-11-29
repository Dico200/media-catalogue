package io.dico.mediacatalogue.menu.searchmenu;

import io.dico.booleanformulaparser.BooleanFormula;
import io.dico.booleanformulaparser.IllegalFormulaException;
import io.dico.mediacatalogue.MediaContainer;
import io.dico.mediacatalogue.media.Media;
import io.dico.mediacatalogue.menu.MenuItem;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class SearchMenuItem extends MenuItem {
    //private static final Map<String, Function<Media, String>> fieldFunctions;
    private static final Map<String, SearchOperator> operators;
    private static final Map<String, Character> booleanFormulaOperators;

    private final MediaContainer mediaContainer;
    private final Consumer<List<Media>> consumer;

    public SearchMenuItem(String name, MediaContainer mediaContainer, Consumer<List<Media>> consumer) {
        super(name);
        this.mediaContainer = mediaContainer;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        console.writeLine("Enter conditions to search by (enter 'help' if you're clueless)");
        String input = console.requestLine();
        if ("help".equals(input)) {
            help();
            return;
        }

        String variableChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        List<String> variables = new LinkedList<>();
        StringBuilder formula = new StringBuilder();
        Pattern pattern = Pattern.compile("&|(\\|)|(<implies>)|(<reverse_implies>)");
        Matcher matcher = pattern.matcher(input);

        int end = 0;
        while (matcher.find()) {
            int start = matcher.start();
            //if (start != matcher.end()) {
            //   throw new IllegalStateException("This shouldn't happen");
            //}

            String variable = input.substring(end, start).trim();
            String operator = input.substring(start, matcher.end());
            if (variable.startsWith("!")) {
                formula.append('!');
                variable = variable.substring(1);
            }

            Character variableChar = booleanFormulaOperators.get(operator);
            if (variableChar == null) {
                throw new IllegalArgumentException("Not an operator?");
            }
            variables.add(variable);
            formula.append(variableChar);
            formula.append(operator);

            end = start;
        }

        String variable = input.substring(end, input.length()).trim();
        if (variable.startsWith("!")) {
            formula.append('!');
            variable = variable.substring(1);
        }

        char variableChar = variableChars.charAt(variables.size());
        variables.add(variable);
        formula.append(variableChar);

        BooleanFormula booleanFormula;
        try {
            booleanFormula = new BooleanFormula(formula.toString());
        } catch (IllegalFormulaException e) {
            throw new IllegalArgumentException("That boolean formula is invalid");
        }

        List<SearchCondition> conditions = variables.stream().map(this::parseCondition).collect(Collectors.toList());
        Predicate<Media> mediaPredicate = media -> {
            boolean[] inputs = new boolean[conditions.size()];
            for (int i = 0; i < inputs.length; i++) {
                inputs[i] = conditions.get(i).test(media);
            }
            return booleanFormula.evaluateFor(inputs);
        };

        List<Media> matches = mediaContainer.stream().filter(mediaPredicate).collect(Collectors.toList());
        consumer.accept(matches);
    }

    private void help() {
        console.writeLine("Search help");
    }

    private String[] splitOnSpaceUnlessQuoted(String input) {
        List<String> result = new LinkedList<>();
        char[] chars = input.toCharArray();

        StringBuilder current = null;
        boolean inQuote = false;
        boolean escapeNext = false;
        for (char c : chars) {
            if (current == null) {
                current = new StringBuilder();
            }
            if (escapeNext) {
                escapeNext = false;
                current.append(c);
            } else if (c == '\\') {
                escapeNext = true;
                current.append(c);
            } else if (c == '"') {
                inQuote = !inQuote;
            } else if (c == ' ' && !inQuote) {
                result.add(current.toString());
                current = null;
            } else {
                current.append(c);
            }
        }
        if (current != null) {
            result.add(current.toString());
        }
        return result.toArray(new String[result.size()]);
    }

    private SearchCondition parseCondition(String condition) {
        condition = condition.trim();
        String[] splitted = splitOnSpaceUnlessQuoted(condition);
        if (splitted.length != 3) {
            throw new IllegalArgumentException("Illegal input length");
        }

        String left = splitted[0].trim();
        String operatorInput = splitted[1];
        String right = splitted[2].trim();

        SearchOperator operator = operators.get(operatorInput);
        if (operator == null) {
            throw new IllegalArgumentException("Operator not found: " + operatorInput);
        }

        return new SearchCondition(left, right, operator);
    }

    static {
        /*
        Map<String, Function<Media, String>> fieldFunctionMap = new HashMap<>();
        fieldFunctionMap.put("type", Media::type);
        fieldFunctionMap.put("title", Media::title);
        fieldFunctionMap.put("releaseyear", media -> Integer.toString(media.releaseYear()));
        fieldFunctionMap.put("rating", media -> Integer.toString(media.rating()));
        fieldFunctionMap.put("getDirector", PartialFunction.onlyForInputClass(Film.class, Film::getDirector));
        fieldFunctionMap.put("studio", PartialFunction.onlyForInputClass(Film.class, Film::studio));
        fieldFunctionMap.put("episode", PartialFunction.onlyForInputClass(TelevisionProgramme.class, TelevisionProgramme::episode));
        fieldFunctionMap.put("channel", PartialFunction.onlyForInputClass(TelevisionProgramme.class, TelevisionProgramme::channel));
        fieldFunctionMap.put("series", PartialFunction.onlyForInputClass(TelevisionProgramme.class, TelevisionProgramme::series));
        fieldFunctionMap.put("artist", PartialFunction.onlyForInputClass(AudioTrack.class, AudioTrack::artist));
        fieldFunctionMap.put("recordlabel", PartialFunction.onlyForInputClass(AudioTrack.class, AudioTrack::recordLabel));
        fieldFunctionMap.put("duration", new PartialFunction<Media, String>() {
            @Override
            public String apply(Media input) throws UndefinedException {
                Duration duration;
                if (input instanceof Film) {
                    duration = ((Film) input).duration();
                } else if (input instanceof AudioTrack) {
                    duration = ((AudioTrack) input).duration();
                } else {
                    throw new UndefinedException();
                }
                return Integer.toString(duration);
            }

            @Override
            public boolean isDefinedFor(Media input) {
                return input instanceof Film || input instanceof AudioTrack;
            }
        });
        fieldFunctionMap.put("studio", new PartialFunction<Media, String>() {
            @Override
            public String apply(Media input) throws UndefinedException {
                if (input instanceof Film) {
                    return ((Film) input).studio();
                }
                if (input instanceof TelevisionProgramme) {
                    return ((TelevisionProgramme) input).studio();
                }
                throw new UndefinedException();
            }

            @Override
            public boolean isDefinedFor(Media input) {
                return input instanceof Film || input instanceof TelevisionProgramme;
            }
        });
        fieldFunctions = Collections.unmodifiableMap(fieldFunctionMap);

        */

        Map<String, Character> booleanFormulaOperatorMap = new HashMap<>();
        booleanFormulaOperatorMap.put("<implies>", '→');
        booleanFormulaOperatorMap.put("<reverse_implies>", '←');
        booleanFormulaOperatorMap.put("&", '^');
        booleanFormulaOperatorMap.put("|", 'v');
        booleanFormulaOperatorMap.put("!", '¬');
        booleanFormulaOperators = Collections.unmodifiableMap(booleanFormulaOperatorMap);

        Function<String, Pattern> patternParser = new Function<String, Pattern>() {
            private Pattern previousPattern;

            @Override
            public Pattern apply(String input) {
                if (previousPattern != null && previousPattern.pattern().equals(input)) {
                    return previousPattern;
                }
                try {
                    return previousPattern = Pattern.compile(input);
                } catch (PatternSyntaxException e) {
                    throw new IllegalArgumentException("Invalid regex: '" + input + "', " + e.getMessage());
                }
            }
        };

        Map<String, SearchOperator> operatorMap = new HashMap<>();
        operatorMap.put("=", SearchOperator.withPredicate("tests if the values are equal", Object::equals));
        operatorMap.put("contains", SearchOperator.withStringPredicate("tests if the first value contains the second", String::contains));

        operatorMap.put(">", SearchOperator.withIntPredicate("tests if the first value is greater than the second", (left, right) -> left > right));
        operatorMap.put("<", SearchOperator.withIntPredicate("tests if the first value is less than the second", (left, right) -> left < right));
        operatorMap.put(">=", SearchOperator.withIntPredicate("tests if the first value is greater than or equal to the second", (left, right) -> left >= right));
        operatorMap.put("<=", SearchOperator.withIntPredicate("tests if the first value is less than or equal to the second", (left, right) -> left <= right));

        operatorMap.put("find", SearchOperator.withStringPredicate("tests if the first value contains any matches for the second", (left, right) -> {
            Pattern pattern = patternParser.apply(right.trim());
            Matcher matcher = pattern.matcher(left.trim());
            return matcher.find();
        }));

        operatorMap.put("matches", SearchOperator.withStringPredicate("tests if the first value matches the second", (left, right) -> {
            Pattern pattern = patternParser.apply(right.trim());
            Matcher matcher = pattern.matcher(left.trim());
            return matcher.matches();
        }));
        operators = Collections.unmodifiableMap(operatorMap);
    }

}

