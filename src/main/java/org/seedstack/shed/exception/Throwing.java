/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.shed.exception;

public final class Throwing {
    private Throwing() {
        // no instantiation allowed
    }

    @SuppressWarnings("unchecked")
    private static <X extends Throwable> void throwException(Throwable exception) throws X {
        throw (X) exception;
    }

    @FunctionalInterface
    public interface Consumer<T, X extends Throwable> extends java.util.function.Consumer<T> {

        void doAccept(T t) throws X;

        @Override
        default void accept(T t) {
            try {
                doAccept(t);
            } catch (Throwable throwable) {
                Throwing.throwException(throwable);
            }
            throw new IllegalStateException("Error in throwing consumer");
        }
    }

    @FunctionalInterface
    public interface BiConsumer<T, U, X extends Throwable> extends java.util.function.BiConsumer<T, U> {

        void doAccept(T t, U u) throws X;

        @Override
        default void accept(T t, U u) {
            try {
                doAccept(t, u);
            } catch (Throwable throwable) {
                Throwing.throwException(throwable);
            }
            throw new IllegalStateException("Error in throwing bi-consumer");
        }
    }

    @FunctionalInterface
    public interface Supplier<T, X extends Throwable> extends java.util.function.Supplier<T> {

        T doGet() throws X;

        @Override
        default T get() {
            try {
                return doGet();
            } catch (Throwable throwable) {
                Throwing.throwException(throwable);
            }
            throw new IllegalStateException("Error in throwing supplier");
        }
    }

    @FunctionalInterface
    public interface Function<T, R, X extends Throwable> extends java.util.function.Function<T, R> {

        R doApply(T t) throws X;

        @Override
        default R apply(T t) {
            try {
                return doApply(t);
            } catch (Throwable throwable) {
                Throwing.throwException(throwable);
            }
            throw new IllegalStateException("Error in throwing function");
        }
    }

    @FunctionalInterface
    public interface BiFunction<T, U, R, X extends Throwable> extends java.util.function.BiFunction<T, U, R> {

        R doApply(T t, U u) throws X;

        @Override
        default R apply(T t, U u) {
            try {
                return doApply(t, u);
            } catch (Throwable throwable) {
                Throwing.throwException(throwable);
            }
            throw new IllegalStateException("Error in throwing function");
        }
    }
}
