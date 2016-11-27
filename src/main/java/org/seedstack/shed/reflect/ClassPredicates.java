/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Predicate;

public final class ClassPredicates {
    private ClassPredicates() {
        // no instantiation allowed
    }

    /**
     * Checks if the candidate is annotated by the specified annotation or meta-annotation.
     *
     * @param annotationClass        the annotation to check for.
     * @param includeMetaAnnotations if true, meta-annotations are included in the search.
     * @return the predicate.
     */
    public static <T extends AnnotatedElement> Predicate<T> elementAnnotatedWith(Class<? extends Annotation> annotationClass, boolean includeMetaAnnotations) {
        return candidate -> {
            if (candidate == null) {
                return false;
            }
            Annotations.OnClass onClass = Annotations.on(candidate);
            if (includeMetaAnnotations) {
                onClass.includingMetaAnnotations();
            }
            return onClass.find(annotationClass).isPresent();
        };
    }

    /**
     * Checks if the candidate is the specified class.
     *
     * @param reference the class to check for.
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIs(final Class<?> reference) {
        return candidate -> candidate != null && candidate.equals(reference);
    }

    /**
     * Check is the candidate is implementing or extending the specified class.
     *
     * @param ancestor the extended or implemented class to check for.
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIsAssignableFrom(Class<?> ancestor) {
        return candidate -> candidate != null && candidate != ancestor && ancestor.isAssignableFrom(candidate);
    }

    /**
     * Checks if the candidate is an interface.
     *
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIsInterface() {
        return candidate -> candidate != null && candidate.isInterface();
    }

    /**
     * Checks if the class is an annotation.
     *
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIsAnnotation() {
        return candidate -> candidate != null && candidate.isAnnotation();
    }

    /**
     * Check if the candidate has the specified modifier.
     *
     * @param modifier the modifier to check for.
     * @return the predicate.
     */
    public static Predicate<Class<?>> classModifierIs(final int modifier) {
        return candidate -> (candidate.getModifiers() & modifier) != 0;
    }

    /**
     * Check if the candidate has the specified modifier.
     *
     * @param modifier the modifier to check for.
     * @return the predicate.
     */
    public static <T extends Executable> Predicate<T> executableModifierIs(final int modifier) {
        return candidate -> (candidate.getModifiers() & modifier) != 0;
    }

    /**
     * Checks if the candidate implements at least one interface..
     *
     * @return the predicate.
     */
    public static Predicate<Class<?>> atLeastOneInterfaceImplemented() {
        return candidate -> candidate != null && candidate.getInterfaces().length > 0;
    }

    /**
     * Check if the specified class has at least a public constructor.
     *
     * @return the predicate.
     */
    public static Predicate<Class<?>> atLeastOneConstructorIsPublic() {
        return candidate -> candidate != null && Classes.from(candidate)
                .constructors()
                .anyMatch(executableModifierIs(Modifier.PUBLIC));
    }

    /**
     * Checks if the candidate or one of its superclasses has at least one field annotated or meta-annotated by the given annotation.
     *
     * @param annotationClass        the requested annotation
     * @param includeMetaAnnotations if true, meta-annotations are included in the search.
     * @return the predicate.
     */
    public static Predicate<Class<?>> atLeastOneFieldAnnotatedWith(final Class<? extends Annotation> annotationClass, boolean includeMetaAnnotations) {
        return candidate -> candidate != null && Classes.from(candidate)
                .traversingSuperclasses()
                .fields()
                .anyMatch(elementAnnotatedWith(annotationClass, includeMetaAnnotations));
    }

    /**
     * Checks if the candidate or one of its superclasses or interfaces has at least one method annotated or meta-annotated
     * by the given annotation.
     *
     * @param annotationClass        the requested annotation
     * @param includeMetaAnnotations if true, meta-annotations are included in the search.
     * @return the predicate.
     */
    public static Predicate<Class<?>> atLeastOneMethodAnnotatedWith(final Class<? extends Annotation> annotationClass, boolean includeMetaAnnotations) {
        return candidate -> Classes.from(candidate)
                .traversingInterfaces()
                .traversingSuperclasses()
                .methods()
                .anyMatch(elementAnnotatedWith(annotationClass, includeMetaAnnotations));

    }

    /**
     * Checks if the candidate has one of its superclasses implementing the specified interface.
     *
     * @param interfaceClass the interface to check for.
     * @return the predicate.
     */
    public static Predicate<Class<?>> ancestorImplements(final Class<?> interfaceClass) {
        return candidate -> candidate != null && candidate.getSuperclass() != null && Classes.from(candidate.getSuperclass())
                .traversingSuperclasses()
                .classes()
                .map(Class::getInterfaces)
                .flatMap(Arrays::stream)
                .anyMatch(Predicate.isEqual(interfaceClass));
    }

    /**
     * Checks if the candidate has one of its superclasses or interfaces annotated with the specified annotation.
     *
     * @param annotationClass        the requested annotation
     * @param includeMetaAnnotations if true, meta-annotations are included in the search.
     * @return the predicate.
     */
    public static Predicate<Class<?>> ancestorAnnotatedWith(final Class<? extends Annotation> annotationClass, boolean includeMetaAnnotations) {
        return candidate -> candidate != null && candidate.getSuperclass() != null && Classes.from(candidate.getSuperclass())
                .traversingSuperclasses()
                .traversingInterfaces()
                .classes()
                .anyMatch(elementAnnotatedWith(annotationClass, includeMetaAnnotations));
    }
}
