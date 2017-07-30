/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;

import org.seedstack.shed.reflect.Classes;

import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public final class ClassPredicates {
    private ClassPredicates() {
        // no instantiation allowed
    }

    /**
     * Checks if a candidate class is equal to the specified class.
     *
     * @param reference the class to check for.
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIs(final Class<?> reference) {
        return candidate -> candidate != null && candidate.equals(reference);
    }

    /**
     * Check if a candidate class is assignable to the specified class.
     *
     * @param ancestor the class to check for.
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIsAssignableFrom(Class<?> ancestor) {
        return candidate -> candidate != null && ancestor.isAssignableFrom(candidate);
    }

    /**
     * Check if a candidate class is strictly a descendant of the specified class (not the specified class itself).
     *
     * @param ancestor the ancestor class to check for.
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIsDescendantOf(Class<?> ancestor) {
        return candidate -> candidate != null && candidate != ancestor && ancestor.isAssignableFrom(candidate);
    }

    /**
     * Checks if a candidate class is an interface.
     *
     * @return the predicate.
     */
    public static Predicate<Class<?>> classImplements(Class<?> anInterface) {
        if (!anInterface.isInterface()) {
            throw new IllegalArgumentException("Class " + anInterface.getName() + " is not an interface");
        }
        return candidate -> candidate != null && !candidate.isInterface() && anInterface.isAssignableFrom(candidate);
    }

    /**
     * Checks if a candidate class is an interface.
     *
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIsInterface() {
        return candidate -> candidate != null && candidate.isInterface();
    }

    /**
     * Checks if a candidate class is an annotation.
     *
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIsAnnotation() {
        return candidate -> candidate != null && candidate.isAnnotation();
    }

    /**
     * Checks if a candidate class has the specified modifier.
     *
     * @param modifier the modifier to check for.
     * @return the predicate.
     */
    public static Predicate<Class<?>> classModifierIs(final int modifier) {
        return candidate -> (candidate.getModifiers() & modifier) != 0;
    }

    /**
     * Checks if a candidate class has the specified modifier.
     *
     * @param modifier the modifier to check for.
     * @return the predicate.
     */
    public static <T extends Executable> Predicate<T> executableModifierIs(final int modifier) {
        return candidate -> (candidate.getModifiers() & modifier) != 0;
    }

    /**
     * Checks if a candidate class implements at least one interface.
     *
     * @return the predicate.
     */
    public static Predicate<Class<?>> atLeastOneInterfaceImplemented() {
        return candidate -> candidate != null && candidate.getInterfaces().length > 0;
    }

    /**
     * Checks if a candidate class has at least one public constructor.
     *
     * @return the predicate.
     */
    public static Predicate<Class<?>> atLeastOneConstructorIsPublic() {
        return candidate -> candidate != null && Classes.from(candidate)
                .constructors()
                .anyMatch(executableModifierIs(Modifier.PUBLIC));
    }
}
