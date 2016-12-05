/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Classes {
    private Classes() {
        // no instantiation allowed
    }

    /**
     * Define the starting point of class reflection.
     *
     * @param someClass the starting class for reflection operations.
     * @return the DSL.
     */
    public static FromClass from(Class<?> someClass) {
        return new FromClass(new Context(someClass));
    }

    /**
     * Checks if a class exists in the classpath.
     *
     * @param dependency class to look for.
     * @return an {@link Optional} of the class (empty if class is not present).
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<Class<T>> optional(String dependency) {
        try {
            return Optional.of((Class<T>) Class.forName(dependency));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public static class End {
        protected final Context context;

        public End(Context context) {
            this.context = context;
        }

        public Stream<Class<?>> classes() {
            return gather(context.getStartingClass());
        }

        public Stream<Constructor<?>> constructors() {
            return classes().map(Class::getDeclaredConstructors).flatMap(Arrays::stream);
        }

        public Stream<Method> methods() {
            return classes().map(Class::getDeclaredMethods).flatMap(Arrays::stream);
        }

        public Stream<Field> fields() {
            return classes().map(Class::getDeclaredFields).flatMap(Arrays::stream);
        }

        private Stream<Class<?>> gather(Class<?>... classes) {
            Stream.Builder<Stream<Class<?>>> builder = Stream.builder();
            for (Class<?> clazz : classes) {
                builder.add(Stream.of(clazz));
                if (context.isIncludeClasses()) {
                    Class<?> superclass = clazz.getSuperclass();
                    if (superclass != null && superclass != Object.class) {
                        builder.add(gather(superclass).filter(context.getPredicate()));
                    }
                }
                if (context.isIncludeInterfaces()) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    if (interfaces.length > 0) {
                        builder.add(gather(interfaces).filter(context.getPredicate()));
                    }
                }
            }
            return builder.build().flatMap(Function.identity());
        }
    }

    public static class FromClass extends End {
        public FromClass(Context context) {
            super(context);
        }

        public FromClass traversingSuperclasses() {
            context.setIncludeClasses(true);
            return this;
        }

        public FromClass traversingInterfaces() {
            context.setIncludeInterfaces(true);
            return this;
        }

        public End filteredBy(Predicate<Class<?>> predicate) {
            context.setPredicate(predicate);
            return this;
        }
    }

    private static class Context {
        private final Class<?> startingClass;
        private boolean includeInterfaces = false;
        private boolean includeClasses = false;
        private Predicate<Class<?>> predicate = (someClass) -> true;

        private Context(Class<?> startingClass) {
            this.startingClass = startingClass;
        }

        public Class<?> getStartingClass() {
            return startingClass;
        }

        public boolean isIncludeInterfaces() {
            return includeInterfaces;
        }

        public Context setIncludeInterfaces(boolean includeInterfaces) {
            this.includeInterfaces = includeInterfaces;
            return this;
        }

        public boolean isIncludeClasses() {
            return includeClasses;
        }

        public Context setIncludeClasses(boolean includeClasses) {
            this.includeClasses = includeClasses;
            return this;
        }

        public Predicate<Class<?>> getPredicate() {
            return predicate;
        }

        public void setPredicate(Predicate<Class<?>> predicate) {
            this.predicate = predicate;
        }
    }
}
