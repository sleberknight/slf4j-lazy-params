package com.fortitudetec.slf4j;

import java.util.function.Supplier;

/**
 * Allows for lazy evaluation of one or more SLF4J replacement parameters in a logging statement.
 */
public class Slf4jParameterSupplier {

    private Slf4jParameterSupplier() {
    }

    /**
     * Delays execution of the {@code original} supplier in a log replacement value. This also permits some
     * replacement parameters to use delayed (lazy) execution while others can be regular parameters.
     * <p>
     * Example usage (assuming static import and using a method reference):
     * <pre>
     *     LOG.debug("Flubber JSON with id {} is: {}", flubber.getId(), delayed(flubber::toJson));
     * </pre>
     *
     * @param original original supplier of a replacement value that might be expensive to compute, e.g. serialize an
     *                 object to JSON
     * @return a {@link Supplier} that wraps {@code original},]
     */
    public static Supplier<String> delayed(Supplier<String> original) {
        return new StringSupplierWrapper(original);
    }

    /**
     * Alias for {@link #delayed(Supplier)}.
     * <p>
     * Example usage (assuming static import and using a method reference):
     * <pre>
     *     LOG.debug("Flubber JSON with id {} is: {}", flubber.getId(), lazy(flubber::toJson));
     * </pre>
     */
    public static Supplier<String> lazy(Supplier<String> original) {
        return delayed(original);
    }

    private static class StringSupplierWrapper implements Supplier<String> {

        private final Supplier<String> original;

        private StringSupplierWrapper(Supplier<String> original) {
            this.original = original;
        }

        @Override
        public String get() {
            return original.get();
        }

        @Override
        public String toString() {
            return get();
        }
    }
}
