package io.dico.mediacatalogue;

import io.dico.mediacatalogue.util.function.UnsafeSupplier;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConsoleOperator {

    private final Supplier<String> in;
    private final Consumer<String> out;

    public ConsoleOperator(Supplier<String> in, Consumer<String> out) {
        this.in = in;
        this.out = out;
    }

    public String requestLine() {
        return in.get();
    }

    public void writeLine(String line) {
        out.accept(line);
    }

    public int requestInt() {
        return requestWithExceptions(() -> Integer.parseInt(requestLine()), "That's not a number, please try again");
    }

    public <R> R requestWithExceptions(UnsafeSupplier<R, Throwable> supplier, String onException) {
        while (true) {
            try {
                return supplier.get();
            } catch (Throwable t) {
                writeLine(onException == null ? t.getMessage() : onException);
            }
        }
    }

    public <R> R requestWithValidator(Supplier<R> supplier, Predicate<R> validator, String ifInvalid) {
        R result;
        while (!validator.test(result = supplier.get())) {
            writeLine(ifInvalid);
        }
        return result;
    }

    public <R> R requestWithExceptionsAndValidator(UnsafeSupplier<R, Throwable> supplier, Predicate<R> validator, String onException, String ifInvalid) {
        return requestWithValidator(() -> requestWithExceptions(supplier, onException), validator, ifInvalid);
    }

    public boolean requestYesOrNo() {
        return "y".equals(requestLine());
    }
}
