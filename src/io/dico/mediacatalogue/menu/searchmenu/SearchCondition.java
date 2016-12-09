package io.dico.mediacatalogue.menu.searchmenu;

import io.dico.mediacatalogue.media.Media;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

abstract class SearchCondition implements Predicate<Media> {
    
    public static SearchCondition withPredicate(String left, String right, BiPredicate<Object, Object> predicate) {
        return new SearchCondition(left, right) {
            @Override
            protected boolean test(Object left, Object right) {
                return predicate.test(left, right);
            }
        };
    }

    private final String left;
    private final String right;

    public SearchCondition(String left, String right) {
        this.left = left;
        this.right = right;
    }

    public boolean test(Media media) {
        Map<String, Object> fields = media.getFields();
        Object leftValue = fields.getOrDefault(left, left);
        Object rightValue = fields.getOrDefault(right, right);
        return test(leftValue, rightValue);
    }
    
    protected abstract boolean test(Object left, Object right);

}
