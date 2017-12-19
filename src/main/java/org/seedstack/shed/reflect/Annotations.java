/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.shed.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.seedstack.shed.cache.Cache;
import org.seedstack.shed.cache.CacheParameters;

public final class Annotations {
    private static final String JAVA_LANG = "java.lang";
    private static Cache<Context, List<Annotation>> cache = Cache.create(
            new CacheParameters<Context, List<Annotation>>()
                    .setInitialSize(256)
                    .setMaxSize(1024)
                    .setLoadingFunction(Context::gather)
    );

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
            return (Optional<T>) findAll().filter(AnnotationPredicates.annotationIsOfClass(annotationClass))
                    .findFirst();
        }

        /**
         * Returns a stream of all the annotations found.
         *
         * @return a stream of annotation objects.
         */
        public Stream<Annotation> findAll() {
            return cache.get(context).stream();
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

    private static final class Context {
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

        void setTraversingInterfaces(boolean traversingInterfaces) {
            this.traversingInterfaces = traversingInterfaces;
        }

        void setTraversingSuperclasses(boolean traversingSuperclasses) {
            this.traversingSuperclasses = traversingSuperclasses;
        }

        void setTraversingOverriddenMembers(boolean traversingOverriddenMembers) {
            this.traversingOverriddenMembers = traversingOverriddenMembers;
        }

        void setFallingBackOnClasses(boolean fallingBackOnClasses) {
            this.fallingBackOnClasses = fallingBackOnClasses;
        }

        void setIncludingMetaAnnotations(boolean includingMetaAnnotations) {
            this.includingMetaAnnotations = includingMetaAnnotations;
        }

        List<Annotation> gather() {
            List<Annotation> annotations = new ArrayList<>(32);
            gather(annotations);
            return annotations;
        }

        private void gather(List<Annotation> list) {
            AnnotatedElement startingAnnotatedElement = annotatedElement;
            List<AnnotatedElement> annotatedElements = new ArrayList<>();
            annotatedElements.add(startingAnnotatedElement);

            if (startingAnnotatedElement instanceof Field) {
                if (fallingBackOnClasses) {
                    annotatedElements.add(((Field) startingAnnotatedElement).getDeclaringClass());
                }
            } else if (startingAnnotatedElement instanceof Method) {
                if (fallingBackOnClasses) {
                    annotatedElements.add(((Method) startingAnnotatedElement).getDeclaringClass());
                }
                if (traversingOverriddenMembers) {
                    Classes.from(((Method) startingAnnotatedElement).getDeclaringClass())
                            .traversingInterfaces()
                            .traversingSuperclasses()
                            .methods()
                            .filter(ExecutablePredicates
                                    .executableIsEquivalentTo(((Method) startingAnnotatedElement)))
                            .forEach(method -> {
                                annotatedElements.add(method);
                                if (fallingBackOnClasses) {
                                    annotatedElements.add(method.getDeclaringClass());
                                }
                            });
                }
            } else if (startingAnnotatedElement instanceof Constructor) {
                if (fallingBackOnClasses) {
                    annotatedElements.add(((Constructor) startingAnnotatedElement).getDeclaringClass());
                }
                if (traversingOverriddenMembers) {
                    Classes.from(((Constructor) startingAnnotatedElement).getDeclaringClass())
                            .traversingSuperclasses()
                            .constructors()
                            .filter(ExecutablePredicates
                                    .executableIsEquivalentTo(((Constructor) startingAnnotatedElement)))
                            .forEach(constructor -> {
                                annotatedElements.add(constructor);
                                if (fallingBackOnClasses) {
                                    annotatedElements.add(constructor.getDeclaringClass());
                                }
                            });
                }
            }

            for (AnnotatedElement annotatedElement : annotatedElements) {
                if (annotatedElement instanceof Class<?> && (traversingInterfaces || traversingSuperclasses)) {
                    Classes.FromClass from = Classes.from(((Class<?>) annotatedElement));
                    if (traversingInterfaces) {
                        from.traversingInterfaces();
                    }
                    if (traversingSuperclasses) {
                        from.traversingSuperclasses();
                    }
                    from.classes().forEach(c -> findAnnotations(c, list));
                } else {
                    findAnnotations(annotatedElement, list);
                }
            }
        }

        private void findAnnotations(AnnotatedElement annotatedElement, List<Annotation> list) {
            for (Annotation annotation : annotatedElement.getAnnotations()) {
                if (!annotation.annotationType().getPackage().getName().startsWith(JAVA_LANG)) {
                    list.add(annotation);
                    if (includingMetaAnnotations) {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || Context.class != o.getClass()) {
                return false;
            }

            Context context = (Context) o;

            if (traversingInterfaces != context.traversingInterfaces) {
                return false;
            }
            if (traversingSuperclasses != context.traversingSuperclasses) {
                return false;
            }
            if (traversingOverriddenMembers != context.traversingOverriddenMembers) {
                return false;
            }
            if (fallingBackOnClasses != context.fallingBackOnClasses) {
                return false;
            }
            if (includingMetaAnnotations != context.includingMetaAnnotations) {
                return false;
            }
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
