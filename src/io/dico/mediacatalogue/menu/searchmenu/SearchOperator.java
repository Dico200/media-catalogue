package io.dico.mediacatalogue.menu.searchmenu;

import io.dico.mediacatalogue.util.function.IntBiPredicate;

import java.util.function.BiPredicate;

abstract class SearchOperator implements BiPredicate<String, String> {

    static SearchOperator withIntPredicate(String description, IntBiPredicate predicate) {
        return new SearchOperator(description) {
            @Override
            public boolean test(String left, String right) {
                try {
                    return predicate.test(Integer.parseInt(left.trim()), Integer.parseInt(right.trim()));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("These must match numbers");
                }
            }
        };
    }

    static SearchOperator withPredicate(String description, BiPredicate<String, String> predicate) {
        return new SearchOperator(description) {
            @Override
            public boolean test(String left, String right) {
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

}
