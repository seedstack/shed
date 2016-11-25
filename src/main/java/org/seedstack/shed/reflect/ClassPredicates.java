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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.function.Predicate;

public class ClassPredicates {
    public static <T extends AnnotatedElement> Predicate<T> elementAnnotatedWith(Class<? extends Annotation> annotationClass) {
        return candidate -> candidate != null && candidate.isAnnotationPresent(annotationClass);
    }

    /**
     * Checks if the candidate isn't equal to the given class.
     *
     * @param reference the class to check.
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIs(final Class<?> reference) {
        return candidate -> candidate != null && candidate.equals(reference);
    }

    /**
     * Checks if the candidate equals to the given class.
     *
     * @param reference the class to check
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIsNot(final Class<?> reference) {
        return candidate -> candidate != null && !candidate.equals(reference);
    }

    /**
     * Check is the candidate is implementing or extending the specified ancestor.
     *
     * @param ancestor the ancestor to look for.
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIsAssignableFrom(Class<?> ancestor) {
        return candidate -> candidate != null && candidate != ancestor && ancestor.isAssignableFrom(candidate);
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
     * Check if the specified class has at least a public constructor.
     *
     * @return the predicate.
     */
    public static Predicate<Class<?>> classConstructorIsPublic() {
        return candidate -> {
            for (Constructor<?> constructor : candidate.getDeclaredConstructors()) {
                if (Modifier.isPublic(constructor.getModifiers())) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * @param interfaceClass the requested interface
     * @return a specification which check if one candidate ancestor implements the given interface
     */
    public static Predicate<Class<?>> ancestorImplements(final Class<?> interfaceClass) {
        return candidate -> {
            if (candidate == null) {
                return false;
            }

            boolean result = false;
            Class<?>[] allInterfacesAndClasses = getAllInterfacesAndClasses(candidate);
            for (Class<?> clazz : allInterfacesAndClasses) {
                if (!clazz.isInterface()) {
                    for (Class<?> i : clazz.getInterfaces()) {
                        if (i.equals(interfaceClass)) {
                            result = true;
                            break;
                        }
                    }
                }
            }

            return result;
        };
    }

    /**
     * Checks if the candidate has one field annotated or meta annotated by the given annotation.
     *
     * @param annotationClass the requested annotation
     * @return the predicate.
     */
    public static Predicate<Class<?>> fieldDeepAnnotatedWith(final Class<? extends Annotation> annotationClass) {
        return candidate -> {
            if (candidate != null) {
                do {
                    for (Field field : candidate.getDeclaredFields()) {
                        if (field.isAnnotationPresent(annotationClass)) {
                            return true;
                        }
                    }
                    candidate = candidate.getSuperclass();
                } while (candidate != null && candidate != Object.class);
            }

            return false;
        };
    }

    /**
     * Checks if the candidate inherits from the given class.
     *
     * @param klass the requested class
     * @return the predicate.
     */
    public static Predicate<Class<?>> classInherits(final Class<?> klass) {
        return candidate -> candidate != null && klass.isAssignableFrom(candidate);
    }

    public static Predicate<Class<?>> classMethodsAnnotatedWith(final Class<? extends Annotation> annotationClass) {
        return new ClassMethodsAnnotatedWith(annotationClass);
    }

    /**
     * Checks if the candidate or an ancestor is annotated or meta annotated by the given annotation.
     *
     * @param anoKlass the requested annotation
     * @return the predicate.
     */
    public static Predicate<Class<?>> ancestorMetaAnnotatedWith(final Class<? extends Annotation> anoKlass) {
        return candidate -> {

            if (candidate == null) {
                return false;
            }

            boolean result = false;

            Class<?>[] allInterfacesAndClasses = getAllInterfacesAndClasses(candidate);

            for (Class<?> clazz : allInterfacesAndClasses) {
                boolean satisfiedBy = classMetaAnnotatedWith(anoKlass).test(clazz);
                if (satisfiedBy) {
                    result = true;
                    break;
                }
            }

            return result;
        };
    }

    /**
     * Checks if the candidate is annotated or meta annotated by the given annotation.
     *
     * @param klass the requested annotation
     * @return the predicate.
     */
    public static Predicate<Class<?>> classMetaAnnotatedWith(final Class<? extends Annotation> klass) {
        return candidate -> candidate != null && hasAnnotationDeep(candidate, klass);

    }

    /**
     * Checks if the given class is annotated or meta annotated with the given annotation.
     *
     * @param aClass          the class to check
     * @param annotationClass the requested annotation
     * @return true if the class if annotated or meta annotated, false otherwise
     */
    public static boolean hasAnnotationDeep(Class<?> aClass, Class<? extends Annotation> annotationClass) {
        if (aClass.equals(annotationClass)) {
            return true;
        }

        for (Annotation anno : aClass.getAnnotations()) {
            Class<? extends Annotation> annoClass = anno.annotationType();
            if (!annoClass.getPackage().getName().startsWith("java.lang")
                    && hasAnnotationDeep(annoClass, annotationClass)) {
                return true;
            }
        }

        return false;
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
     * Checks if the candidate has interface.
     *
     * @return the predicate.
     */
    public static Predicate<Class<?>> classHasSuperInterfaces() {
        return candidate -> candidate != null && candidate.getInterfaces().length > 0;
    }

    /**
     * Checks if the class is an annotation
     *
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIsAnnotation() {
        return candidate -> candidate != null && candidate.isAnnotation();
    }

    /**
     * Checks if the class is abstract.
     *
     * @return the predicate.
     */
    public static Predicate<Class<?>> classIsAbstract() {
        return candidate -> candidate != null && Modifier.isAbstract(candidate.getModifiers());
    }

    /**
     * Checks if at least one method of the class is annotated with the annotation class.
     *
     * @param annotationClass the requested annotation
     * @return the predicate.
     */
    public static Predicate<Class<?>> atLeastOneMethodAnnotatedWith(final Class<? extends Annotation> annotationClass) {
        return candidate -> !Classes.from(candidate)
                .traversingInterfaces()
                .traversingSuperclasses()
                .methods(elementAnnotatedWith(annotationClass)).isEmpty();

    }
}
