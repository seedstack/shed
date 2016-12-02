/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.predicate;

import org.seedstack.shed.reflect.Annotations;
import org.seedstack.shed.reflect.Classes;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Predicate;

public final class AnnotationPredicates {
    private AnnotationPredicates() {
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
     * Checks if the candidate is annotated by the specified annotation or meta-annotation.
     *
     * @param annotationClass        the annotation to check for.
     * @param includeMetaAnnotations if true, meta-annotations are included in the search.
     * @return the predicate.
     */
    public static <T extends Annotation> Predicate<T> annotationAnnotatedWith(Class<? extends Annotation> annotationClass, boolean includeMetaAnnotations) {
        return candidate -> elementAnnotatedWith(annotationClass, includeMetaAnnotations).test(candidate.annotationType());
    }

    /**
     * Checks if the candidate or one of its superclasses or interfaces is annotated with the specified annotation.
     *
     * @param annotationClass        the requested annotation
     * @param includeMetaAnnotations if true, meta-annotations are included in the search.
     * @return the predicate.
     */
    public static Predicate<Class<?>> classOrAncestorAnnotatedWith(final Class<? extends Annotation> annotationClass, boolean includeMetaAnnotations) {
        return candidate -> candidate != null && Classes.from(candidate)
                .traversingSuperclasses()
                .traversingInterfaces()
                .classes()
                .anyMatch(elementAnnotatedWith(annotationClass, includeMetaAnnotations));
    }

    /**
     * Checks if the candidate annotation is of the specified annotation class.
     *
     * @param annotationClass the annotation class to check for.
     * @return the predicate.
     */
    public static Predicate<Annotation> annotationIsOfClass(final Class<? extends Annotation> annotationClass) {
        return candidate -> candidate != null && candidate.annotationType().equals(annotationClass);
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
}
