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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public final class Classes {
    private Classes() {
        // no instantiation allowed
    }

    public static FromClass from(Class<?> someClass) {
        return new FromClass(new Context(someClass));
    }

    public static class End {
        protected final Context context;

        public End(Context context) {
            this.context = context;
        }

        public List<Class<?>> classes() {
            return gather(context.getStartingClass());
        }

        public List<Constructor<?>> constructors() {
            return constructors((constructor) -> true);
        }

        public List<Constructor<?>> constructors(Predicate<Constructor<?>> predicate) {
            List<Constructor<?>> constructors = new ArrayList<>();
            for (Class<?> aClass : classes()) {
                Arrays.stream(aClass.getDeclaredConstructors()).filter(predicate).forEachOrdered(constructors::add);
            }
            return constructors;
        }

        public List<Method> methods() {
            return methods((method) -> true);
        }

        public List<Method> methods(Predicate<Method> predicate) {
            List<Method> methods = new ArrayList<>();
            for (Class<?> aClass : classes()) {
                Arrays.stream(aClass.getDeclaredMethods()).filter(predicate).forEachOrdered(methods::add);
            }
            return methods;
        }

        public List<Field> fields() {
            return fields((field) -> true);
        }

        public List<Field> fields(Predicate<Field> predicate) {
            List<Field> fields = new ArrayList<>();
            for (Class<?> aClass : classes()) {
                Arrays.stream(aClass.getDeclaredFields()).filter(predicate).forEachOrdered(fields::add);
            }
            return fields;
        }

        private List<Class<?>> gather(Class<?>... classes) {
            List<Class<?>> result = new ArrayList<>();
            Arrays.stream(classes).forEach(clazz -> {
                result.add(clazz);
                if (context.isIncludeClasses()) {
                    Class<?> superclass = clazz.getSuperclass();
                    if (superclass != null && superclass != Object.class) {
                        gather(superclass).stream().filter(context.getPredicate()).forEachOrdered(result::add);
                    }
                }
                if (context.isIncludeInterfaces()) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    if (interfaces.length > 0) {
                        gather(interfaces).stream().filter(context.getPredicate()).forEachOrdered(result::add);
                    }
                }
            });
            return result;
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
