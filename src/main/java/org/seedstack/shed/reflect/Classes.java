/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Classes {
    private static ConcurrentMap<Context, List<Class<?>>> classesCache = new ConcurrentHashMap<>(1024);

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
        final Context context;

        End(Context context) {
            this.context = context;
        }

        @SuppressFBWarnings(value = "RV_RETURN_VALUE_OF_PUTIFABSENT_IGNORED", justification = "using putIfAbsent for concurrency")
        public Stream<Class<?>> classes() {
            List<Class<?>> classes = classesCache.get(context);
            if (classes == null) {
                classesCache.putIfAbsent(context, classes = gather(context.getStartingClass(), new ArrayList<>(32)));
            }
            return classes.stream();
        }

        public Stream<Constructor<?>> constructors() {
            return classes().map(Class::getDeclaredConstructors).flatMap(Arrays::stream);
        }

        public Optional<? extends Constructor<?>> constructor(Class<?>... parameterTypes) {
            return constructors()
                    .filter(constructor -> Arrays.equals(constructor.getParameterTypes(), parameterTypes))
                    .findFirst();
        }

        public Stream<Method> methods() {
            return classes().map(Class::getDeclaredMethods).flatMap(Arrays::stream);
        }

        public Optional<Method> method(String name, Class<?>... parameterTypes) {
            return methods()
                    .filter(method -> method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), parameterTypes))
                    .findFirst();
        }

        public Stream<Field> fields() {
            return classes().map(Class::getDeclaredFields).flatMap(Arrays::stream);
        }

        public Optional<Field> field(String name) {
            return fields()
                    .filter(field -> field.getName().equals(name))
                    .findFirst();
        }

        private List<Class<?>> gather(Class<?> aClass, List<Class<?>> list) {
            list.add(aClass);
            if (context.isIncludeInterfaces()) {
                for (Class<?> anInterface : aClass.getInterfaces()) {
                    Predicate<Class<?>> predicate = context.getPredicate();
                    if (predicate == null || predicate.test(anInterface)) {
                        gather(anInterface, list);
                    }
                }
            }
            if (context.isIncludeClasses()) {
                Class<?> superclass = aClass.getSuperclass();
                if (superclass != null && superclass != Object.class) {
                    Predicate<Class<?>> predicate = context.getPredicate();
                    if (predicate == null || predicate.test(superclass)) {
                        gather(superclass, list);
                    }
                }
            }
            return list;
        }
    }

    public static class FromClass extends End {
        FromClass(Context context) {
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
        private Predicate<Class<?>> predicate;

        private Context(Class<?> startingClass) {
            this.startingClass = startingClass;
        }

        Class<?> getStartingClass() {
            return startingClass;
        }

        boolean isIncludeInterfaces() {
            return includeInterfaces;
        }

        void setIncludeInterfaces(boolean includeInterfaces) {
            this.includeInterfaces = includeInterfaces;
        }

        boolean isIncludeClasses() {
            return includeClasses;
        }

        void setIncludeClasses(boolean includeClasses) {
            this.includeClasses = includeClasses;
        }

        Predicate<Class<?>> getPredicate() {
            return predicate;
        }

        void setPredicate(Predicate<Class<?>> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Context context = (Context) o;
            return includeInterfaces == context.includeInterfaces &&
                    includeClasses == context.includeClasses &&
                    Objects.equals(startingClass, context.startingClass) &&
                    Objects.equals(predicate, context.predicate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startingClass, includeInterfaces, includeClasses, predicate);
        }
    }
}
