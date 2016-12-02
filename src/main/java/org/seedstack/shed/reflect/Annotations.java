/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;

import org.seedstack.shed.predicate.AnnotationPredicates;
import org.seedstack.shed.predicate.ExecutablePredicates;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

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

    public static OnExecutable on(Executable someExecutable) {
        return new OnExecutable(new Context(someExecutable));
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

        @SuppressWarnings("unchecked")
        public <T extends Annotation> Optional<T> find(Class<T> annotationClass) {
            return (Optional<T>) findAll().filter(AnnotationPredicates.annotationIsOfClass(annotationClass)).findFirst();
        }

        public Stream<Annotation> findAll() {
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
                            .filter(ExecutablePredicates.executableIsEquivalentTo(((Method) startingAnnotatedElement)))
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
                            .filter(ExecutablePredicates.executableIsEquivalentTo(((Constructor) startingAnnotatedElement)))
                            .forEach(constructor -> {
                                annotatedElements.add(constructor);
                                if (context.isFallingBackOnClasses()) {
                                    annotatedElements.add(constructor.getDeclaringClass());
                                }
                            });
                }
            }

            Stream.Builder<Stream<Annotation>> builder = Stream.builder();
            for (AnnotatedElement annotatedElement : annotatedElements) {
                if (annotatedElement instanceof Class<?> && (context.isTraversingInterfaces() || context.isTraversingSuperclasses())) {
                    Classes.FromClass from = Classes.from(((Class<?>) annotatedElement));
                    if (context.isTraversingInterfaces()) {
                        from.traversingInterfaces();
                    }
                    if (context.isTraversingSuperclasses()) {
                        from.traversingSuperclasses();
                    }
                    builder.add(from.classes().map(this::findAnnotations).flatMap(Function.identity()));
                } else {
                    builder.add(findAnnotations(annotatedElement));
                }
            }
            return builder.build().flatMap(Function.identity());
        }

        private Stream<Annotation> findAnnotations(AnnotatedElement annotatedElement) {
            Stream.Builder<Stream<Annotation>> builder = Stream.builder();
            for (Annotation annotation : annotatedElement.getAnnotations()) {
                if (!annotation.annotationType().getPackage().getName().startsWith(JAVA_LANG)) {
                    builder.add(Stream.of(annotation));
                    if (context.isIncludingMetaAnnotations()) {
                        builder.add(findMetaAnnotations(annotation));
                    }
                }
            }
            return builder.build().flatMap(Function.identity());
        }

        private Stream<Annotation> findMetaAnnotations(Annotation from) {
            Stream.Builder<Stream<Annotation>> builder = Stream.builder();
            for (Annotation annotation : from.annotationType().getAnnotations()) {
                if (!annotation.annotationType().getPackage().getName().startsWith(JAVA_LANG)) {
                    builder.add(Stream.concat(Stream.of(annotation), findMetaAnnotations(annotation)));
                }
            }
            return builder.build().flatMap(Function.identity());
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

    public static class OnExecutable extends OnAnnotatedElement {
        public OnExecutable(Context context) {
            super(context);
        }

        public OnExecutable traversingOverriddenMembers() {
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
