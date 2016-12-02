/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.predicate;

import org.junit.Test;

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
}
