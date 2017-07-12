/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public final class Annotations {
    private static final String JAVA_LANG = "java.lang";
    // This is an unbounded cache
    private static ConcurrentMap<Context, List<Annotation>> cache = new ConcurrentHashMap<>(1024);

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
        final Context context;

        OnClass(Context context) {
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

        @SuppressFBWarnings(value = "RV_RETURN_VALUE_OF_PUTIFABSENT_IGNORED", justification = "using putIfAbsent for concurrency")
        public Stream<Annotation> findAll() {
            List<Annotation> annotations = cache.get(context);
            if (annotations == null) {
                cache.putIfAbsent(context, annotations = doFindAll(new ArrayList<>(32)));
            }
            return annotations.stream();
        }

        private List<Annotation> doFindAll(List<Annotation> list) {
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

            for (AnnotatedElement annotatedElement : annotatedElements) {
                if (annotatedElement instanceof Class<?> && (context.isTraversingInterfaces() || context.isTraversingSuperclasses())) {
                    Classes.FromClass from = Classes.from(((Class<?>) annotatedElement));
                    if (context.isTraversingInterfaces()) {
                        from.traversingInterfaces();
                    }
                    if (context.isTraversingSuperclasses()) {
                        from.traversingSuperclasses();
                    }
                    from.classes().forEach(c -> findAnnotations(c, list));
                } else {
                    findAnnotations(annotatedElement, list);
                }
            }

            return list;
        }

        private void findAnnotations(AnnotatedElement annotatedElement, List<Annotation> list) {
            for (Annotation annotation : annotatedElement.getAnnotations()) {
                if (!annotation.annotationType().getPackage().getName().startsWith(JAVA_LANG)) {
                    list.add(annotation);
                    if (context.isIncludingMetaAnnotations()) {
                        findMetaAnnotations(annotation, list);
                    }
                }
            }
        }

        private void findMetaAnnotations(Annotation from, List<Annotation> list) {
            for (Annotation annotation : from.annotationType().getAnnotations()) {
                if (!annotation.annotationType().getPackage().getName().startsWith(JAVA_LANG)) {
                    list.add(annotation);
                    findMetaAnnotations(annotation, list);
                }
            }
        }
    }

    public static class OnAnnotatedElement extends OnClass {
        OnAnnotatedElement(Context context) {
            super(context);
        }

        public OnClass fallingBackOnClasses() {
            context.setFallingBackOnClasses(true);
            return this;
        }
    }

    public static class OnExecutable extends OnAnnotatedElement {
        OnExecutable(Context context) {
            super(context);
        }

        public OnExecutable traversingOverriddenMembers() {
            context.setTraversingOverriddenMembers(true);
            return this;
        }
    }

    private final static class Context {
        private final AnnotatedElement annotatedElement;
        private boolean traversingInterfaces = false;
        private boolean traversingSuperclasses = false;
        private boolean traversingOverriddenMembers = false;
        private boolean fallingBackOnClasses = false;
        private boolean includingMetaAnnotations = false;
        private int hashCode;

        private Context(AnnotatedElement annotatedElement) {
            this.annotatedElement = annotatedElement;
        }

        AnnotatedElement getAnnotatedElement() {
            return annotatedElement;
        }

        boolean isTraversingInterfaces() {
            return traversingInterfaces;
        }

        void setTraversingInterfaces(boolean traversingInterfaces) {
            this.traversingInterfaces = traversingInterfaces;
        }

        boolean isTraversingSuperclasses() {
            return traversingSuperclasses;
        }

        void setTraversingSuperclasses(boolean traversingSuperclasses) {
            this.traversingSuperclasses = traversingSuperclasses;
        }

        boolean isTraversingOverriddenMembers() {
            return traversingOverriddenMembers;
        }

        void setTraversingOverriddenMembers(boolean traversingOverriddenMembers) {
            this.traversingOverriddenMembers = traversingOverriddenMembers;
        }

        boolean isFallingBackOnClasses() {
            return fallingBackOnClasses;
        }

        void setFallingBackOnClasses(boolean fallingBackOnClasses) {
            this.fallingBackOnClasses = fallingBackOnClasses;
        }

        boolean isIncludingMetaAnnotations() {
            return includingMetaAnnotations;
        }

        void setIncludingMetaAnnotations(boolean includingMetaAnnotations) {
            this.includingMetaAnnotations = includingMetaAnnotations;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || Context.class != o.getClass()) return false;

            Context context = (Context) o;

            if (traversingInterfaces != context.traversingInterfaces) return false;
            if (traversingSuperclasses != context.traversingSuperclasses) return false;
            if (traversingOverriddenMembers != context.traversingOverriddenMembers) return false;
            if (fallingBackOnClasses != context.fallingBackOnClasses) return false;
            if (includingMetaAnnotations != context.includingMetaAnnotations) return false;
            return annotatedElement.equals(context.annotatedElement);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = annotatedElement.hashCode();
                result = 31 * result + (traversingInterfaces ? 1 : 0);
                result = 31 * result + (traversingSuperclasses ? 1 : 0);
                result = 31 * result + (traversingOverriddenMembers ? 1 : 0);
                result = 31 * result + (fallingBackOnClasses ? 1 : 0);
                result = 31 * result + (includingMetaAnnotations ? 1 : 0);
                hashCode = result;
            }
            return result;
        }
    }
}
