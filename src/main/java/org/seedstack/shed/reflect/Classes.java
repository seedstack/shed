/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.shed.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.seedstack.shed.cache.Cache;
import org.seedstack.shed.cache.CacheParameters;
import org.seedstack.shed.internal.ShedErrorCode;
import org.seedstack.shed.internal.ShedException;

public final class Classes {
    private static Cache<Context, List<Class<?>>> cache = Cache.create(
            new CacheParameters<Context, List<Class<?>>>()
                    .setInitialSize(256)
                    .setMaxSize(1024)
                    .setLoadingFunction(Context::gather)
    );

    private Classes() {
        // no instantiation allowed
    }

    /**
     * Instantiate a class by invoking its default constructor. If the specified class denotes an
     * array, an empty array of the correct component type is created. If the specified class denotes
     * a primitive, the primitive is created with its default value.
     *
     * @param someClass the class to instantiate.
     * @param <T>       the type of the object to instantiate.
     * @return the instantiated object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T instantiateDefault(Class<T> someClass) {
        if (someClass.isArray()) {
            return (T) Array.newInstance(someClass.getComponentType(), 0);
        } else {
            if (boolean.class.equals(someClass) || Boolean.class.equals(someClass)) {
                return (T) Boolean.FALSE;
            } else if (int.class.equals(someClass) || Integer.class.equals(someClass)) {
                return (T) Integer.valueOf(0);
            } else if (long.class.equals(someClass) || Long.class.equals(someClass)) {
                return (T) Long.valueOf(0L);
            } else if (short.class.equals(someClass) || Short.class.equals(someClass)) {
                return (T) Short.valueOf((short) 0);
            } else if (float.class.equals(someClass) || Float.class.equals(someClass)) {
                return (T) Float.valueOf(0f);
            } else if (double.class.equals(someClass) || Double.class.equals(someClass)) {
                return (T) Double.valueOf(0d);
            } else if (byte.class.equals(someClass) || Byte.class.equals(someClass)) {
                return (T) Byte.valueOf((byte) 0);
            } else if (char.class.equals(someClass) || Character.class.equals(someClass)) {
                return (T) Character.valueOf((char) 0);
            } else {
                try {
                    Constructor<T> defaultConstructor = someClass.getDeclaredConstructor();
                    defaultConstructor.setAccessible(true);
                    return defaultConstructor.newInstance();
                } catch (Exception e) {
                    throw ShedException.wrap(e, ShedErrorCode.UNABLE_TO_INSTANTIATE_CLASS)
                            .put("class", someClass);
                }
            }
        }
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

    /**
     * Cast the specified class to a class parameterized with any generic type.
     *
     * @param someClass the class to cast.
     * @param <T>       the type to cast to.
     * @return the casted class.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Class<?>> T cast(Class<?> someClass) {
        return (T) someClass;
    }

    /**
     * Cast the specified object to an object of the specified type.
     *
     * @param object the object to cast.
     * @param <T>    the type to cast to.
     * @return the casted object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object) {
        return (T) object;
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

    public static class End {
        final Context context;

        End(Context context) {
            this.context = context;
        }

        /**
         * Returns a stream of all the classes found.
         *
         * @return a stream of class objects.
         */
        public Stream<Class<?>> classes() {
            return cache.get(context).stream();
        }

        /**
         * Returns a stream of all the constructors found.
         *
         * @return a stream of constructor objects.
         */
        public Stream<Constructor<?>> constructors() {
            return classes().map(Class::getDeclaredConstructors).flatMap(Arrays::stream);
        }

        /**
         * Returns the first constructyor of the specified parameter types found if any.
         *
         * @param parameterTypes the constructor parameter types.
         * @return an optional containing the constructor if found.
         */
        public Optional<? extends Constructor<?>> constructor(Class<?>... parameterTypes) {
            return constructors()
                    .filter(constructor -> Arrays.equals(constructor.getParameterTypes(), parameterTypes))
                    .findFirst();
        }

        /**
         * Returns a stream of all the methods found.
         *
         * @return a stream of method objects.
         */
        public Stream<Method> methods() {
            return classes().map(Class::getDeclaredMethods).flatMap(Arrays::stream);
        }

        /**
         * Returns the first method of the specified name and parameter types found if any.
         *
         * @param name           the name of the method.
         * @param parameterTypes the method parameter types.
         * @return an optional containing the method if found.
         */
        public Optional<Method> method(String name, Class<?>... parameterTypes) {
            return methods()
                    .filter(method -> method.getName().equals(name) && Arrays
                            .equals(method.getParameterTypes(), parameterTypes))
                    .findFirst();
        }

        /**
         * Returns a stream of all the fields found.
         *
         * @return a stream of field objects.
         */
        public Stream<Field> fields() {
            return classes().map(Class::getDeclaredFields).flatMap(Arrays::stream);
        }

        /**
         * Returns the first field of the specified name found if any.
         *
         * @param name the name of the field.
         * @return an optional containing the field if found.
         */
        public Optional<Field> field(String name) {
            return fields()
                    .filter(field -> field.getName().equals(name))
                    .findFirst();
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
    }

    private static final class Context {
        private final Class<?> startingClass;
        private boolean includeInterfaces = false;
        private boolean includeClasses = false;
        private int hashCode;

        private Context(Class<?> startingClass) {
            this.startingClass = startingClass;
        }

        void setIncludeInterfaces(boolean includeInterfaces) {
            this.includeInterfaces = includeInterfaces;
        }

        void setIncludeClasses(boolean includeClasses) {
            this.includeClasses = includeClasses;
        }

        List<Class<?>> gather() {
            List<Class<?>> classes = new ArrayList<>(32);
            gather(startingClass, classes);
            return classes;
        }

        private void gather(Class<?> someClass, List<Class<?>> list) {
            list.add(someClass);
            if (includeInterfaces) {
                for (Class<?> anInterface : someClass.getInterfaces()) {
                    gather(anInterface, list);
                }
            }
            if (includeClasses) {
                Class<?> superclass = someClass.getSuperclass();
                if (superclass != null && superclass != Object.class) {
                    gather(superclass, list);
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

            if (includeInterfaces != context.includeInterfaces) {
                return false;
            }
            if (includeClasses != context.includeClasses) {
                return false;
            }
            return startingClass == context.startingClass;
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = startingClass.hashCode();
                result = 31 * result + (includeInterfaces ? 1 : 0);
                result = 31 * result + (includeClasses ? 1 : 0);
                hashCode = result;
            }
            return result;
        }
    }
}
