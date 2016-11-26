package io.dico.mediacatalogue.menu.searchmenu;

import io.dico.mediacatalogue.media.Media;
import io.dico.mediacatalogue.util.function.UndefinedException;

import java.util.function.Function;

class SearchCondition {

    private final Function<Media, String> leftFunction;
    private final Function<Media, String> rightFunction;
    private final SearchOperator predicate;

    public SearchCondition(Function<Media, String> left, Function<Media, String> right, SearchOperator predicate) {
        this.leftFunction = left;
        this.rightFunction = right;
        this.predicate = predicate;
    }

    public boolean test(Media media) {
        String left;
        try {
            left = leftFunction.apply(media);
        } catch (UndefinedException e) {
            return false;
        }

        String right;
        try {
            right = rightFunction.apply(media);
        } catch (UndefinedException e) {
            return false;
        }

        return predicate.test(left, right);
    }

}
