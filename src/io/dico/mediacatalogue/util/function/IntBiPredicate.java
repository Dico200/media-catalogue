package io.dico.mediacatalogue.util.function;

@FunctionalInterface
public interface IntBiPredicate {
    boolean test(int left, int right);
}
