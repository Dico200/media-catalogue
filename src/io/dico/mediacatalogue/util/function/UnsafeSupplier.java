package io.dico.mediacatalogue.util.function;

/**
 * A supplier whose get() method may throw an exception
 * @param <T> The type returned by get()
 * @param <E> The type or a supertype of the exception thrown
 */
@FunctionalInterface
public interface UnsafeSupplier<T, E extends Throwable> {

    /**
     * Computes a result, this computation may throw an exception
     * @return the result of this computation
     * @throws E if an error occurred during this computation
     */
    T get() throws E;
}
