package io.dico.mediacatalogue.menu.searchmenu;

import io.dico.booleanformulaparser.BooleanFormula;
import io.dico.booleanformulaparser.IllegalFormulaException;
import io.dico.mediacatalogue.media.Media;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Criteria implements Predicate<Media> {
    
    private static final String variableChars;
    private static final Map<String, SearchOperator> searchOperators;
    private static final Map<String, Character> booleanFormulaOperators;
    
    public static Map<String, SearchOperator> getSearchOperators() {
        return searchOperators;
    }
    
    public static Map<String, Character> getBooleanFormulaOperators() {
        return booleanFormulaOperators;
    }
    
    public static Criteria parse(String inputString) {
        List<SearchCondition> conditions = new LinkedList<>();
        StringBuilder formula = new StringBuilder();
        
        String[] inputSplit = splitOnSpaceUnlessQuoted(inputString);
        if ((inputSplit.length - 3) % 4 != 0) {
            // allow lengths of 3, 7, 11, 15, etc.
            // these are conditions (3 space-separated inputs) separated by a boolean formula operator.
            throw new IllegalArgumentException("That boolean formula is invalid");
        }
        
        // current index in the input string
        int index = 0;
        // we switch this around every iteration
        // condition, operator, condition, operator, condition...
        boolean operatorNext = false;
        while (index < inputSplit.length) {
            if (operatorNext) {
                // parse an operator
                String input = inputSplit[index];
                Character operator = booleanFormulaOperators.get(input.toLowerCase());
                if (operator == null) {
                    throw new IllegalArgumentException("\"" + input + "\" is not a boolean operator");
                }
                formula.append(operator);
                index++;
                operatorNext = false;
                continue;
            }
            
            // parse a condition
            String searchOperator = inputSplit[index + 1];
            if (searchOperator.startsWith("!")) {
                inputSplit[index + 1] = searchOperator.substring(1);
                formula.append(booleanFormulaOperators.get("!"));
            }
            
            String[] conditionInput = Arrays.copyOfRange(inputSplit, index, index + 3);
            
            char variableRepresentationChar = variableChars.charAt(conditions.size());
            formula.append(variableRepresentationChar);
            SearchCondition newCondition = parseCondition(conditionInput);
            conditions.add(newCondition);
            
            operatorNext = true;
            index += 3;
        }
        
        BooleanFormula booleanFormula;
        try {
            booleanFormula = new BooleanFormula(formula.toString());
        } catch (IllegalFormulaException e) {
            throw new IllegalArgumentException("That boolean formula is invalid");
        }

        return new Criteria(conditions, booleanFormula);
    }
    
    private static String[] splitOnSpaceUnlessQuoted(String input) {
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
                // the previous character was a \. This means that we want to ignore any special functionality of the next character,
                // and append it to the input. This allows you to add quotes inside of a quoted argument, and you can use a space in a non-quoted argument as well.
                escapeNext = false;
                current.append(c);
            } else if (c == '\\') {
                escapeNext = true;
            } else if (c == '"') {
                //If this code is executed, the quote is not being escaped.
                //With this implementation, the user can write "tit"le, to pass the argument 'title', which is okay.
                inQuote = !inQuote;
            } else if (c == ' ' && !inQuote && current.length() != 0) {
                // we split the formula on spaces. If the current length is 0, we just split it, and we should ignore this space.
                // add the built up argument to the list of arguments.
                result.add(current.toString());
                current = null;
            } else {
                // If none of the above was the case, just append the character. This will happen most of the time.
                current.append(c);
            }
        }
        // the last argument most likely wasn't space-terminated, so we have to add this code here.
        // allows the user not to terminate the last argument with a quote character if it started with one. That's okay too.
        if (current != null) {
            result.add(current.toString());
        }
        return result.toArray(new String[result.size()]);
    }
    
    private final List<SearchCondition> conditions;
    private final BooleanFormula booleanFormula;
    
    public Criteria(List<SearchCondition> conditions, BooleanFormula booleanFormula) {
        this.conditions = conditions;
        this.booleanFormula = booleanFormula;
    }
    
    public List<SearchCondition> getConditions() {
        return conditions;
    }
    
    public BooleanFormula getBooleanFormula() {
        return booleanFormula;
    }
    
    @Override
    public boolean test(Media media) {
        // pass the test results from the conditions to the formula
        boolean[] inputs = new boolean[conditions.size()];
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = conditions.get(i).test(media);
        }
        return booleanFormula.evaluateFor(inputs);
    }
    
    // parse an input string to a search condition
    // a search condition is an input string, a search operator and another input string, separated by spaces (unless quoted).
    // if either values are the same as one of the fields in a media object, it is replaced with a representation of that field.
    private static SearchCondition parseCondition(String[] condition) {
        if (condition.length != 3) {
            // this shouldn't happen but who knows it might.
            throw new IllegalArgumentException("Illegal input length");
        }
        
        String left = condition[0];
        String operatorInput = condition[1];
        String right = condition[2];
        
        SearchOperator operator = searchOperators.get(operatorInput.toLowerCase());
        if (operator == null) {
            throw new IllegalArgumentException("Operator not found: " + operatorInput);
        }
        
        return SearchCondition.withPredicate(left, right, operator);
    }
    
    static {
        // these characters are place holders for 'true' and 'false' in the boolean formula library.
        // as a result, the amount of search conditions is limited to 26.
        variableChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        // The boolean formula library uses mathematically correct characters, which you won't be able to find on your keyboard most of the time
        // I decided to map some other, more accessible sequences.
        Map<String, Character> booleanFormulaOperatorMap = new HashMap<>();
        booleanFormulaOperatorMap.put("<implies>", '→');
        booleanFormulaOperatorMap.put("<reverse_implies>", '←');
        booleanFormulaOperatorMap.put("&", '^');
        booleanFormulaOperatorMap.put("|", 'v');
        booleanFormulaOperatorMap.put("!", '¬');
        booleanFormulaOperators = Collections.unmodifiableMap(booleanFormulaOperatorMap);
        
        // a function used in the next piece of code. It caches the previous pattern, so it's not compiled many times
        // in the same search operation. Not the best implementation, but it speeds it up significantly.
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
        
        // the search operators.
        Map<String, SearchOperator> operatorMap = new HashMap<>();
        operatorMap.put("=", SearchOperator.withStringPredicate("tests if the values are equal", String::equals));
        operatorMap.put("contains", SearchOperator.withStringPredicate("tests if the first value contains the second", String::contains));
        
        operatorMap.put(">", SearchOperator.withIntPredicate("tests if the first value is greater than the second", (left, right) -> left > right));
        operatorMap.put("<", SearchOperator.withIntPredicate("tests if the first value is less than the second", (left, right) -> left < right));
        operatorMap.put(">=", SearchOperator.withIntPredicate("tests if the first value is greater than or equal to the second", (left, right) -> left >= right));
        operatorMap.put("<=", SearchOperator.withIntPredicate("tests if the first value is less than or equal to the second", (left, right) -> left <= right));
        
        operatorMap.put("find", SearchOperator.withStringPredicate("tests if the first value contains any matches for the second value, which is a regex", (left, right) -> {
            Pattern pattern = patternParser.apply(right.trim());
            Matcher matcher = pattern.matcher(left.trim());
            return matcher.find();
        }));
        
        operatorMap.put("matches", SearchOperator.withStringPredicate("tests if the first value matches the second value, which is a regex", (left, right) -> {
            Pattern pattern = patternParser.apply(right.trim());
            Matcher matcher = pattern.matcher(left.trim());
            return matcher.matches();
        }));
        searchOperators = Collections.unmodifiableMap(operatorMap);
    }
    
}
