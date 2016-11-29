package io.dico.mediacatalogue;

import io.dico.mediacatalogue.util.function.UnsafeSupplier;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConsoleOperator {

    private final Supplier<String> in;
    private final Consumer<String> out;

    /**
     * Constructs a new Console Operator, using the given Supplier as input and the given Consumer as output
     * @param in the Supplier, which is expected to buffer until a newline character
     * @param out The output, which is expected to print newline characters when it receives input
     */
    public ConsoleOperator(Supplier<String> in, Consumer<String> out) {
        this.in = in;
        this.out = out;
    }

    /**
     * @return The supplier used by this console operator to read from the console
     */
    public Supplier<String> getIn() {
        return in;
    }

    /**
     * @return The consumer used by this console operator to write to the console
     */
    public Consumer<String> getOut() {
        return out;
    }

    /**
     * Buffers characters written into the console until the next new line
     * @return the characters written
     */
    public String requestLine() {
        return in.get();
    }

    /**
     * writes a line to the console
     * @param line the line to write
     */
    public void writeLine(String line) {
        out.accept(line);
    }

    /**
     * Requests an integer from the console without telling it that an integer is expected.
     * Integers can be submitted only by newlines, and the method returns only when a valid integer is submitted.
     * If an invalid integer is submitted, the string "That's not a number, please try again" is written to the console.
     * @return The submitted integer once it is submitted
     */
    public int requestInt() {
        return requestWithExceptions(() -> Integer.parseInt(requestLine()), "That's not a number, please try again");
    }

    /**
     * Requests items from the Supplier until the output passes the Predicate.
     * If the output does not pass the predicate, a custom message is written to the console
     * @param supplier The Supplier giving inputs. It is expected to use the console. If this supplier never returns
     *                 a value that passes the predicate, this method will not return.
     * @param validator The predicate used to check the returned value with
     * @param ifInvalid The string written to the console if the predicate returns false
     * @param <R> The type of object computed by the supplier and returned by this method
     * @return A value returned from the supplier that passes the predicate
     */
    public <R> R requestWithValidator(Supplier<R> supplier, Predicate<R> validator, String ifInvalid) {
        R result;
        while (!validator.test(result = supplier.get())) {
            writeLine(ifInvalid);
        }
        return result;
    }

    /**
     * Same as requestWithValidator, only the condition isn't given by a predicate, but by whether the supplier
     * threw an exception during its computation. If it does, it writes a message to the console and requests new input.
     * @param supplier The supplier used by this method to compute values
     * @param onException The message written to the console if an error occurred during the supplier's computation.
     *                    If null, the the String returned by getMessage() from the exception thrown is written instead.
     * @param <R> The type of object computed by the supplier and returned by this method
     * @param <E> The type of exception expected from the supplier
     * @return A return value from the supplier
     */
    public <R, E extends Throwable> R requestWithExceptions(UnsafeSupplier<R, E> supplier, String onException) {
        while (true) {
            try {
                return supplier.get();
            } catch (Throwable e) {
                try {
                    // can't check with instanceof when using generics
                    // so try casting and if it fails, throw the exception again
                    E exception = (E) e;
                    writeLine(onException == null ? exception.getMessage() : onException);
                    // loop continues to request again
                } catch (ClassCastException e2) {
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * requestWithValidator and requestWithExceptions in one. First, the method computes a result with the supplier.
     * If that fails, a message is written to the console by the same rules as requestWithExceptions.
     * Then, the result must pass the predicate. If not, writer a message by the rules of requestWithValidator to the console and start over.
     */
    public <R, E extends Throwable> R requestWithValidatorAndExceptions(UnsafeSupplier<R, E> supplier,  Predicate<R> validator, String onException, String ifInvalid) {
        return requestWithValidator(() -> requestWithExceptions(supplier, onException), validator, ifInvalid);
    }

    /**
     * Requests a line from the console and checks it against "y"
     * @return true if the line on the console equals "y"
     */
    public boolean requestYesOrNo() {
        return "y".equals(requestLine());
    }
}
