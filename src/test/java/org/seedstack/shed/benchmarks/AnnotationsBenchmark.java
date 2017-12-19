/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.seedstack.shed.reflect.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@State(Scope.Benchmark)
public class AnnotationsBenchmark {
    private static Method notAnnotatedMethod;

    static {
        try {
            notAnnotatedMethod = MetaAnnotatedByInterface.class.getDeclaredMethod("notAnnotatedMethod");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void findMetaAnnotationByInterface() {
        Annotations.on(notAnnotatedMethod)
                .fallingBackOnClasses()
                .includingMetaAnnotations()
                .traversingSuperclasses()
                .traversingInterfaces()
                .find(TypeAnnotation.class)
                .get();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    private @interface TypeAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @TypeAnnotation
    private @interface TypeMetaAnnotation {
    }

    @TypeMetaAnnotation
    private interface MetaAnnotatedInterface {
        void notAnnotatedMethod();
    }

    private static class MetaAnnotatedByInterface implements MetaAnnotatedInterface {
        Object notAnnotatedField;

        MetaAnnotatedByInterface() {
        }

        public void notAnnotatedMethod() {
        }

        void notAnnotatedLocalMethod() {
        }
    }
}
