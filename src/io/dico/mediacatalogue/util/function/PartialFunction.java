package io.dico.mediacatalogue.util.function;

import java.util.function.Function;

/**
 * A function that applies only to a range of possible inputs
 *
 * @param <T> The input type
 * @param <R> The output type
 */
public interface PartialFunction<T, R> extends Function<T, R> {

    static <T, U extends T, R> PartialFunction<T, R> onlyForInputClass(Class<U> clazz, Function<U, R> delegate) {
        return new PartialFunction<T, R>() {

            @Override
            public R apply(T input) {
                if (!isDefinedFor(input)) {
                    throw new UndefinedException();
                }
                return delegate.apply(clazz.cast(input));
            }

            @Override
            public boolean isDefinedFor(T input) {
                return clazz.isInstance(input);
            }

        };
    }

    /* *
     * Composes this partial function and the given partial function into one partial function.
     * Inputs that are defined for both functions will favor this partial function.
     * @param other The partial function to compose with
     * @return A partial function that is composed of this partial function and the given partial function
     * /
    default <U> PartialFunction<? extends T & U, R> or(PartialFunction<U, R> other) {
        PartialFunction<T , R> first = this;
        return new PartialFunction<Object, R>() {
            @Override
            public R apply(Object input) throws UndefinedException {
                if (first.isDefinedFor(input)) {
                    return first.apply(input);
                }
                if (other.isDefinedFor(input)) {
                    return other.apply(input);
                }
                throw new UndefinedException();
            }

            @Override
            public boolean isDefinedFor(T input) {
                return first.isDefinedFor(input) || other.isDefinedFor(input);
            }
        };
    }*/

    /**
     * @param input the input of this computation
     * @return the result of this computation
     * @throws UndefinedException if isDefinedFor(input) returns false
     */
    R apply(T input) throws UndefinedException;

    boolean isDefinedFor(T input);

}
