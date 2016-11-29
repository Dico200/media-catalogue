package io.dico.mediacatalogue.menu.searchmenu;

import io.dico.mediacatalogue.util.Duration;
import io.dico.mediacatalogue.util.function.IntBiPredicate;

import java.util.function.BiPredicate;

abstract class SearchOperator implements BiPredicate<Object, Object> {

    static SearchOperator withIntPredicate(String description, IntBiPredicate predicate) {
        return new SearchOperator(description) {
            private int valueOf(Object object) {
                if (object instanceof Number) {
                    return ((Number) object).intValue();
                }
                boolean isString = object instanceof String;
                String stringValue = isString ? (String) object : object.toString();
                try {
                    return Integer.parseInt(stringValue);
                } catch (NumberFormatException e) {
                    if (isString) {
                        return Duration.fromString((String) object).intValue();
                    }
                    throw e;
                }
            }

            @Override
            public boolean test(Object left, Object right) {
                try {
                    return predicate.test(valueOf(left), valueOf(right));
                } catch (NullPointerException | IllegalArgumentException e) {
                    return false;
                }
            }
        };
    }

    static SearchOperator withStringPredicate(String description, BiPredicate<String, String> predicate) {
        return new SearchOperator(description) {
            private String valueOf(Object object) {
                return object instanceof String ? (String) object : String.valueOf(object);
            }

            @Override
            public boolean test(Object left, Object right) {
                return predicate.test(valueOf(left), valueOf(right));
            }
        };
    }

    static SearchOperator withPredicate(String description, BiPredicate<Object, Object> predicate) {
        return new SearchOperator(description) {
            @Override
            public boolean test(Object left, Object right) {
                return predicate.test(left, right);
            }
        };
    }

    private final String description;

    private SearchOperator(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public abstract boolean test(Object left, Object right);

}
