/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;

import org.junit.Test;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ClassPredicatesTest {
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
        assertThat(ClassPredicates.elementAnnotatedWith(SomeAnnotation.class, false).test(AnnotatedElements.class)).isTrue();
        assertThat(ClassPredicates.elementAnnotatedWith(SomeAnnotation.class, false).test(AnnotatedElements.class.getDeclaredField("someField"))).isTrue();
        assertThat(ClassPredicates.elementAnnotatedWith(SomeAnnotation.class, false).test(AnnotatedElements.class.getDeclaredMethod("someMethod"))).isTrue();
        assertThat(ClassPredicates.elementAnnotatedWith(SomeMetaAnnotation.class, false).test(AnnotatedElements.class)).isFalse();
        assertThat(ClassPredicates.elementAnnotatedWith(SomeMetaAnnotation.class, false).test(AnnotatedElements.class.getDeclaredField("someField"))).isFalse();
        assertThat(ClassPredicates.elementAnnotatedWith(SomeMetaAnnotation.class, false).test(AnnotatedElements.class.getDeclaredMethod("someMethod"))).isFalse();
        assertThat(ClassPredicates.elementAnnotatedWith(SomeMetaAnnotation.class, true).test(AnnotatedElements.class)).isTrue();
        assertThat(ClassPredicates.elementAnnotatedWith(SomeMetaAnnotation.class, true).test(AnnotatedElements.class.getDeclaredField("someField"))).isTrue();
        assertThat(ClassPredicates.elementAnnotatedWith(SomeMetaAnnotation.class, true).test(AnnotatedElements.class.getDeclaredMethod("someMethod"))).isTrue();
    }

    @Test
    public void classIs() throws Exception {
        assertThat(ClassPredicates.classIs(SomeAnnotation.class).test(SomeAnnotation.class)).isTrue();
        assertThat(ClassPredicates.classIs(SomeAnnotation.class).test(SomeMetaAnnotation.class)).isFalse();
    }

    @Test
    public void classIsAssignable() throws Exception {
        assertThat(ClassPredicates.classIsAssignableFrom(Number.class).test(Integer.class)).isTrue();
        assertThat(ClassPredicates.classIsAssignableFrom(Number.class).test(String.class)).isFalse();
    }

    @Test
    public void classIsInterface() throws Exception {
        assertThat(ClassPredicates.classIsInterface().test(Collection.class)).isTrue();
        assertThat(ClassPredicates.classIsInterface().test(ArrayList.class)).isFalse();
    }

    @Test
    public void classIsAnnotation() throws Exception {
        assertThat(ClassPredicates.classIsAnnotation().test(SomeAnnotation.class)).isTrue();
        assertThat(ClassPredicates.classIsInterface().test(String.class)).isFalse();
    }

    @Test
    public void classHasModifierIs() throws Exception {
        assertThat(ClassPredicates.classModifierIs(Modifier.ABSTRACT).test(AbstractCollection.class)).isTrue();
        assertThat(ClassPredicates.classModifierIs(Modifier.ABSTRACT).test(ArrayList.class)).isFalse();
    }

    @Test
    public void atLeastOneInterfaceImplemented() throws Exception {
        assertThat(ClassPredicates.atLeastOneInterfaceImplemented().test(ArrayList.class)).isTrue();
        assertThat(ClassPredicates.atLeastOneInterfaceImplemented().test(Object.class)).isFalse();
    }

    @Test
    public void atLeastOneConstructorIsPublic() throws Exception {
        assertThat(ClassPredicates.atLeastOneConstructorIsPublic().test(ClassPredicates.class)).isFalse();
        assertThat(ClassPredicates.atLeastOneConstructorIsPublic().test(String.class)).isTrue();
    }

    @Test
    public void atLeastOneMethodAnnotatedWith() throws Exception {
        assertThat(ClassPredicates.atLeastOneMethodAnnotatedWith(SomeAnnotation.class, false).test(AnnotatedElements.class)).isTrue();
        assertThat(ClassPredicates.atLeastOneMethodAnnotatedWith(SomeMetaAnnotation.class, false).test(AnnotatedElements.class)).isFalse();
        assertThat(ClassPredicates.atLeastOneMethodAnnotatedWith(SomeMetaAnnotation.class, true).test(AnnotatedElements.class)).isTrue();
    }

    @Test
    public void atLeastOneFieldAnnotatedWith() throws Exception {
        assertThat(ClassPredicates.atLeastOneFieldAnnotatedWith(SomeAnnotation.class, false).test(AnnotatedElements.class)).isTrue();
        assertThat(ClassPredicates.atLeastOneFieldAnnotatedWith(SomeMetaAnnotation.class, false).test(AnnotatedElements.class)).isFalse();
        assertThat(ClassPredicates.atLeastOneFieldAnnotatedWith(SomeMetaAnnotation.class, true).test(AnnotatedElements.class)).isTrue();
    }

    @Test
    public void ancestorImplements() throws Exception {
        assertThat(ClassPredicates.ancestorImplements(Collection.class).test(ArrayList.class)).isTrue();
        assertThat(ClassPredicates.ancestorImplements(Serializable.class).test(ArrayList.class)).isFalse();
    }

    @Test
    public void ancestorAnnotatedWith() throws Exception {
        assertThat(ClassPredicates.ancestorAnnotatedWith(SomeAnnotation.class, false).test(ExtendingAnnotatedElements.class)).isTrue();
        assertThat(ClassPredicates.ancestorAnnotatedWith(SomeMetaAnnotation.class, false).test(ExtendingAnnotatedElements.class)).isFalse();
        assertThat(ClassPredicates.ancestorAnnotatedWith(SomeMetaAnnotation.class, true).test(ExtendingAnnotatedElements.class)).isTrue();
    }
}
