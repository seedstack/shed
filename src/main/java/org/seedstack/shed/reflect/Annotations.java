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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public final class Annotations {
    private static final String JAVA_LANG = "java.lang";

    private Annotations() {
        // no instantiation allowed
    }

    public static OnAnnotatedElement on(AnnotatedElement annotatedElement) {
        return new OnAnnotatedElement(new Context(annotatedElement));
    }

    public static OnAnnotatedElement on(Field field) {
        return new OnAnnotatedElement(new Context(field));
    }

    public static OnMethodOrConstructor on(Method someMethod) {
        return new OnMethodOrConstructor(new Context(someMethod));
    }

    public static OnMethodOrConstructor on(Constructor<?> someConstructor) {
        return new OnMethodOrConstructor(new Context(someConstructor));
    }

    public static OnClass on(Class<?> someClass) {
        return new OnClass(new Context(someClass));
    }

    public static class OnClass {
        protected final Context context;

        public OnClass(Context context) {
            this.context = context;
        }

        public OnClass traversingSuperclasses() {
            context.setTraversingSuperclasses(true);
            return this;
        }

        public OnClass traversingInterfaces() {
            context.setTraversingInterfaces(true);
            return this;
        }

        public OnClass includingMetaAnnotations() {
            context.setIncludingMetaAnnotations(true);
            return this;
        }

        public <T extends Annotation> Optional<T> find(Class<T> annotationClass) {
            AnnotatedElement startingAnnotatedElement = context.getAnnotatedElement();
            List<AnnotatedElement> annotatedElements = new ArrayList<>();
            annotatedElements.add(startingAnnotatedElement);

            if (startingAnnotatedElement instanceof Field) {
                if (context.isFallingBackOnClasses()) {
                    annotatedElements.add(((Field) startingAnnotatedElement).getDeclaringClass());
                }
            } else if (startingAnnotatedElement instanceof Method) {
                if (context.isFallingBackOnClasses()) {
                    annotatedElements.add(((Method) startingAnnotatedElement).getDeclaringClass());
                }
                if (context.isTraversingOverriddenMembers()) {
                    Classes.from(((Method) startingAnnotatedElement).getDeclaringClass())
                            .traversingInterfaces()
                            .traversingSuperclasses()
                            .methods()
                            .filter(method -> methodsAreEquivalent(method, ((Method) startingAnnotatedElement)))
                            .forEach(method -> {
                                annotatedElements.add(method);
                                if (context.isFallingBackOnClasses()) {
                                    annotatedElements.add(method.getDeclaringClass());
                                }
                            });
                }
            } else if (startingAnnotatedElement instanceof Constructor) {
                if (context.isFallingBackOnClasses()) {
                    annotatedElements.add(((Constructor) startingAnnotatedElement).getDeclaringClass());
                }
                if (context.isTraversingOverriddenMembers()) {
                    Classes.from(((Constructor) startingAnnotatedElement).getDeclaringClass())
                            .traversingSuperclasses()
                            .constructors()
                            .filter(Predicate.isEqual(startingAnnotatedElement))
                            .forEach(constructor -> {
                                annotatedElements.add(constructor);
                                if (context.isFallingBackOnClasses()) {
                                    annotatedElements.add(constructor.getDeclaringClass());
                                }
                            });
                }
            }

            Optional<T> matchingAnnotation;
            for (AnnotatedElement annotatedElement : annotatedElements) {
                if (annotatedElement instanceof Class<?> && (context.isTraversingInterfaces() || context.isTraversingSuperclasses())) {
                    Classes.FromClass from = Classes.from(((Class<?>) annotatedElement));
                    if (context.isTraversingInterfaces()) {
                        from.traversingInterfaces();
                    }
                    if (context.isTraversingSuperclasses()) {
                        from.traversingSuperclasses();
                    }
                    matchingAnnotation = from.classes()
                            .map(traversedClass -> findAnnotation(traversedClass, annotationClass))
                            .filter(Objects::nonNull)
                            .findFirst();
                } else {
                    matchingAnnotation = Optional.ofNullable(findAnnotation(annotatedElement, annotationClass));
                }

                // short circuit if found
                if (matchingAnnotation.isPresent()) {
                    return matchingAnnotation;
                }
            }
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        private <T extends Annotation> T findAnnotation(AnnotatedElement annotatedElement, Class<T> toFind) {
            Annotation result = annotatedElement.getAnnotation(toFind);
            if (result == null && context.isIncludingMetaAnnotations()) {
                for (Annotation candidateAnnotation : annotatedElement.getAnnotations()) {
                    result = findAnnotation(candidateAnnotation, toFind);
                    if (result != null) {
                        break;
                    }
                }
            }
            return (T) result;
        }

        @SuppressWarnings("unchecked")
        private <T extends Annotation> T findAnnotation(Annotation from, Class<T> toFind) {
            if (toFind.isAssignableFrom(from.annotationType())) {
                return (T) from;
            } else {
                for (Annotation anno : from.annotationType().getAnnotations()) {
                    if (!anno.annotationType().getPackage().getName().startsWith(JAVA_LANG)) {
                        return findAnnotation(anno, toFind);
                    }
                }
            }
            return null;
        }

        private boolean methodsAreEquivalent(Method left, Method right) {
            return left.getName().equals(right.getName()) &&
                    left.getReturnType().equals(right.getReturnType()) &&
                    methodsHaveSameParameterTypes(left, right);
        }

        private boolean methodsHaveSameParameterTypes(Method left, Method right) {
            Class<?>[] leftParameterTypes = left.getParameterTypes();
            Class<?>[] rightParameterTypes = right.getParameterTypes();
            if (leftParameterTypes.length != rightParameterTypes.length) {
                return false;
            }
            for (int i = 0; i < leftParameterTypes.length; i++) {
                if (!leftParameterTypes[i].equals(rightParameterTypes[i])) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class OnAnnotatedElement extends OnClass {
        public OnAnnotatedElement(Context context) {
            super(context);
        }

        public OnClass fallingBackOnClasses() {
            context.setFallingBackOnClasses(true);
            return this;
        }
    }

    public static class OnMethodOrConstructor extends OnAnnotatedElement {
        public OnMethodOrConstructor(Context context) {
            super(context);
        }

        public OnMethodOrConstructor traversingOverriddenMembers() {
            context.setTraversingOverriddenMembers(true);
            return this;
        }
    }

    private static class Context {
        private final AnnotatedElement annotatedElement;
        private boolean traversingInterfaces = false;
        private boolean traversingSuperclasses = false;
        private boolean traversingOverriddenMembers = false;
        private boolean fallingBackOnClasses = false;
        private boolean includingMetaAnnotations = false;

        private Context(AnnotatedElement annotatedElement) {
            this.annotatedElement = annotatedElement;
        }

        public AnnotatedElement getAnnotatedElement() {
            return annotatedElement;
        }

        public boolean isTraversingInterfaces() {
            return traversingInterfaces;
        }

        public void setTraversingInterfaces(boolean traversingInterfaces) {
            this.traversingInterfaces = traversingInterfaces;
        }

        public boolean isTraversingSuperclasses() {
            return traversingSuperclasses;
        }

        public void setTraversingSuperclasses(boolean traversingSuperclasses) {
            this.traversingSuperclasses = traversingSuperclasses;
        }

        public boolean isTraversingOverriddenMembers() {
            return traversingOverriddenMembers;
        }

        public void setTraversingOverriddenMembers(boolean traversingOverriddenMembers) {
            this.traversingOverriddenMembers = traversingOverriddenMembers;
        }

        public boolean isFallingBackOnClasses() {
            return fallingBackOnClasses;
        }

        public void setFallingBackOnClasses(boolean fallingBackOnClasses) {
            this.fallingBackOnClasses = fallingBackOnClasses;
        }

        public boolean isIncludingMetaAnnotations() {
            return includingMetaAnnotations;
        }

        public void setIncludingMetaAnnotations(boolean includingMetaAnnotations) {
            this.includingMetaAnnotations = includingMetaAnnotations;
        }
    }
}
