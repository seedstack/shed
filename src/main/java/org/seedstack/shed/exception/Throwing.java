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

        void apply(T t) throws X;

        @Override
        default void accept(T t) {
            try {
                apply(t);
            } catch (Throwable throwable) {
                Throwing.throwException(throwable);
            }
        }
    }
}
