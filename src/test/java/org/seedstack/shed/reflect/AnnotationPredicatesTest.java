/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;

import org.junit.Test;
import org.seedstack.shed.reflect.AnnotationPredicates;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AnnotationPredicatesTest {
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.ANNOTATION_TYPE})
    private @interface SomeMetaAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
    @SomeMetaAnnotation
    private @interface SomeAnnotation {
    }

    @SomeAnnotation
    private static class AnnotatedElements {
        @SomeAnnotation
        private String someField;

        @SomeAnnotation
        private void someMethod() {

        }
    }

    private static class ExtendingAnnotatedElements extends AnnotatedElements {

    }

    @Test
    public void elementAnnotatedWith() throws Exception {
        assertThat(AnnotationPredicates.elementAnnotatedWith(SomeAnnotation.class, false).test(AnnotatedElements.class)).isTrue();
        assertThat(AnnotationPredicates.elementAnnotatedWith(SomeAnnotation.class, false).test(AnnotatedElements.class.getDeclaredField("someField"))).isTrue();
        assertThat(AnnotationPredicates.elementAnnotatedWith(SomeAnnotation.class, false).test(AnnotatedElements.class.getDeclaredMethod("someMethod"))).isTrue();
        assertThat(AnnotationPredicates.elementAnnotatedWith(SomeMetaAnnotation.class, false).test(AnnotatedElements.class)).isFalse();
        assertThat(AnnotationPredicates.elementAnnotatedWith(SomeMetaAnnotation.class, false).test(AnnotatedElements.class.getDeclaredField("someField"))).isFalse();
        assertThat(AnnotationPredicates.elementAnnotatedWith(SomeMetaAnnotation.class, false).test(AnnotatedElements.class.getDeclaredMethod("someMethod"))).isFalse();
        assertThat(AnnotationPredicates.elementAnnotatedWith(SomeMetaAnnotation.class, true).test(AnnotatedElements.class)).isTrue();
        assertThat(AnnotationPredicates.elementAnnotatedWith(SomeMetaAnnotation.class, true).test(AnnotatedElements.class.getDeclaredField("someField"))).isTrue();
        assertThat(AnnotationPredicates.elementAnnotatedWith(SomeMetaAnnotation.class, true).test(AnnotatedElements.class.getDeclaredMethod("someMethod"))).isTrue();
    }

    @Test
    public void atLeastOneMethodAnnotatedWith() throws Exception {
        assertThat(AnnotationPredicates.atLeastOneMethodAnnotatedWith(SomeAnnotation.class, false).test(AnnotatedElements.class)).isTrue();
        assertThat(AnnotationPredicates.atLeastOneMethodAnnotatedWith(SomeMetaAnnotation.class, false).test(AnnotatedElements.class)).isFalse();
        assertThat(AnnotationPredicates.atLeastOneMethodAnnotatedWith(SomeMetaAnnotation.class, true).test(AnnotatedElements.class)).isTrue();
    }

    @Test
    public void atLeastOneFieldAnnotatedWith() throws Exception {
        assertThat(AnnotationPredicates.atLeastOneFieldAnnotatedWith(SomeAnnotation.class, false).test(AnnotatedElements.class)).isTrue();
        assertThat(AnnotationPredicates.atLeastOneFieldAnnotatedWith(SomeMetaAnnotation.class, false).test(AnnotatedElements.class)).isFalse();
        assertThat(AnnotationPredicates.atLeastOneFieldAnnotatedWith(SomeMetaAnnotation.class, true).test(AnnotatedElements.class)).isTrue();
    }

    @Test
    public void elementOrAncestorAnnotatedWith() throws Exception {
        assertThat(AnnotationPredicates.classOrAncestorAnnotatedWith(SomeAnnotation.class, false).test(ExtendingAnnotatedElements.class)).isTrue();
        assertThat(AnnotationPredicates.classOrAncestorAnnotatedWith(SomeMetaAnnotation.class, false).test(ExtendingAnnotatedElements.class)).isFalse();
        assertThat(AnnotationPredicates.classOrAncestorAnnotatedWith(SomeMetaAnnotation.class, true).test(ExtendingAnnotatedElements.class)).isTrue();
    }
}
