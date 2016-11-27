/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public final class MethodPredicates {
    private MethodPredicates() {
        // no instantiation allowed
    }

    /**
     * Check if the specified method is not synthetic.
     *
     * @return the predicate.
     */
    public static Predicate<Method> methodIsSynthetic() {
        return candidate -> candidate != null && candidate.isSynthetic();
    }

    /**
     * Check if the specified method is not synthetic.
     *
     * @return the predicate.
     */
    public static Predicate<Method> methodIsBridge() {
        return candidate -> candidate != null && candidate.isBridge();
    }

    /**
     * Check if the specified method is not synthetic.
     *
     * @return the predicate.
     */
    public static Predicate<Method> methodIsOfObject() {
        return candidate -> candidate != null && Object.class.equals(candidate.getDeclaringClass());
    }
}
