package io.dico.mediacatalogue.menu.searchmenu;

import io.dico.mediacatalogue.media.Media;

import java.util.Map;

class SearchCondition {

    private final String left;
    private final String right;
    private final SearchOperator predicate;

    public SearchCondition(String left, String right, SearchOperator predicate) {
        this.left = left;
        this.right = right;
        this.predicate = predicate;
    }

    public boolean test(Media media) {
        Map<String, Object> fields = media.getFields();
        Object leftValue = fields.getOrDefault(left, left);
        Object rightValue = fields.getOrDefault(right, right);
        return predicate.test(leftValue, rightValue);
    }

}
