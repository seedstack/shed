/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;

import org.junit.Test;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AnnotationsTest {
    @Test
    public void notAnnotated() throws Exception {
        checkNotAnnotated(NotAnnotatedClass.class, TypeAnnotation.class, false);
        checkNotAnnotated(NotAnnotatedClass.class, TypeMetaAnnotation.class, true);
    }

    private void checkNotAnnotated(Class<?> classToCheck, Class<? extends Annotation> annotationClass, boolean meta)
            throws NoSuchFieldException, NoSuchMethodException {
        assertThat(Annotations.on(classToCheck).find(annotationClass)).isNotPresent();
        assertThat(Annotations.on(classToCheck).traversingSuperclasses().traversingInterfaces().find(annotationClass)
        ).isNotPresent();

        Field notAnnotatedField = classToCheck.getDeclaredField("notAnnotatedField");
        assertThat(on(notAnnotatedField, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedField, true, meta).find(annotationClass)).isNotPresent();

        Method notAnnotatedMethod = classToCheck.getDeclaredMethod("notAnnotatedMethod");
        assertThat(on(notAnnotatedMethod, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedMethod, true, meta).find(annotationClass)).isNotPresent();

        Constructor<?> notAnnotatedConstructor = classToCheck.getDeclaredConstructor();
        assertThat(on(notAnnotatedConstructor, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedConstructor, true, meta).find(annotationClass)).isNotPresent();
    }

    @Test
    public void annotatedClass() throws Exception {
        checkAnnotatedClass(AnnotatedBaseClass.class, TypeAnnotation.class, false);
    }

    @Test
    public void metaAnnotatedClass() throws Exception {
        checkAnnotatedClass(MetaAnnotatedBaseClass.class, TypeMetaAnnotation.class, false);
        checkAnnotatedClass(MetaAnnotatedBaseClass.class, TypeAnnotation.class, true);
    }

    private void checkAnnotatedClass(Class<?> classToCheck, Class<? extends Annotation> annotationClass, boolean
            meta) throws NoSuchFieldException, NoSuchMethodException {
        assertThat(on(classToCheck, false, meta).find(annotationClass)).isPresent();

        Field notAnnotatedField = classToCheck.getDeclaredField("notAnnotatedField");
        assertThat(on(notAnnotatedField, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedField, true, meta).find(annotationClass)).isPresent();

        Method notAnnotatedMethod = classToCheck.getDeclaredMethod("notAnnotatedMethod");
        assertThat(on(notAnnotatedMethod, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedMethod, true, meta).find(annotationClass)).isPresent();

        Constructor<?> notAnnotatedConstructor = classToCheck.getDeclaredConstructor();
        assertThat(on(notAnnotatedConstructor, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedConstructor, true, meta).find(annotationClass)).isPresent();
    }

    @Test
    public void annotatedByBaseClass() throws Exception {
        checkAnnotatedByBaseClass(AnnotatedByBaseClass.class, TypeAnnotation.class, false);
    }

    @Test
    public void metaAnnotatedByBaseClass() throws Exception {
        checkAnnotatedByBaseClass(MetaAnnotatedByBaseClass.class, TypeMetaAnnotation.class, false);
        checkAnnotatedByBaseClass(MetaAnnotatedByBaseClass.class, TypeAnnotation.class, true);
    }

    private void checkAnnotatedByBaseClass(Class<?> classToCheck, Class<? extends Annotation> annotationClass,
                                           boolean meta) throws NoSuchFieldException, NoSuchMethodException {
        assertThat(on(classToCheck, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(classToCheck, false, meta).traversingSuperclasses().find(annotationClass)).isPresent();
        assertThat(on(classToCheck, false, meta).traversingInterfaces().find(annotationClass)).isNotPresent();
        assertThat(on(classToCheck, false, meta).traversingSuperclasses().traversingInterfaces().find
                (annotationClass)).isPresent();

        Field notAnnotatedField = classToCheck.getDeclaredField("notAnnotatedField");
        assertThat(on(notAnnotatedField, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedField, true, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedField, true, meta).traversingSuperclasses().find(annotationClass)).isPresent();
        assertThat(on(notAnnotatedField, true, meta).traversingInterfaces().find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedField, true, meta).traversingSuperclasses().traversingInterfaces().find
                (annotationClass)).isPresent();

        Method notAnnotatedMethod = classToCheck.getDeclaredMethod("notAnnotatedMethod");
        assertThat(on(notAnnotatedMethod, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedMethod, true, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedMethod, true, meta).traversingSuperclasses().find(annotationClass)).isPresent();
        assertThat(on(notAnnotatedMethod, true, meta).traversingInterfaces().find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedMethod, true, meta).traversingSuperclasses().traversingInterfaces().find
                (annotationClass)).isPresent();

        Constructor<?> notAnnotatedConstructor = classToCheck.getDeclaredConstructor();
        assertThat(on(notAnnotatedConstructor, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedConstructor, true, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedConstructor, true, meta).traversingSuperclasses().find(annotationClass)).isPresent();
        assertThat(on(notAnnotatedConstructor, true, meta).traversingInterfaces().find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedConstructor, true, meta).traversingSuperclasses().traversingInterfaces().find
                (annotationClass)).isPresent();
    }

    @Test
    public void annotatedByInterface() throws Exception {
        checkAnnotatedByInterface(AnnotatedByInterface.class, TypeAnnotation.class, false);
    }

    @Test
    public void metaAnnotatedByInterface() throws Exception {
        checkAnnotatedByInterface(MetaAnnotatedByInterface.class, TypeMetaAnnotation.class, false);
        checkAnnotatedByInterface(MetaAnnotatedByInterface.class, TypeAnnotation.class, true);
    }

    private void checkAnnotatedByInterface(Class<?> classToCheck, Class<? extends Annotation> annotationClass,
                                           boolean meta) throws NoSuchFieldException, NoSuchMethodException {
        assertThat(on(classToCheck, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(classToCheck, false, meta).traversingSuperclasses().find(annotationClass)).isNotPresent();
        assertThat(on(classToCheck, false, meta).traversingInterfaces().find(annotationClass)).isPresent();
        assertThat(on(classToCheck, false, meta).traversingSuperclasses().traversingInterfaces().find
                (annotationClass)).isPresent();

        Field notAnnotatedField = classToCheck.getDeclaredField("notAnnotatedField");
        assertThat(on(notAnnotatedField, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedField, true, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedField, true, meta).traversingSuperclasses().find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedField, true, meta).traversingInterfaces().find(annotationClass)).isPresent();
        assertThat(on(notAnnotatedField, true, meta).traversingSuperclasses().traversingInterfaces().find
                (annotationClass)).isPresent();

        Method notAnnotatedMethod = classToCheck.getDeclaredMethod("notAnnotatedMethod");
        assertThat(on(notAnnotatedMethod, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedMethod, true, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedMethod, true, meta).traversingSuperclasses().find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedMethod, true, meta).traversingInterfaces().find(annotationClass)).isPresent();
        assertThat(on(notAnnotatedMethod, true, meta).traversingSuperclasses().traversingInterfaces().find
                (annotationClass)).isPresent();

        Constructor<?> notAnnotatedConstructor = classToCheck.getDeclaredConstructor();
        assertThat(on(notAnnotatedConstructor, false, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedConstructor, true, meta).find(annotationClass)).isNotPresent();
        assertThat(on(notAnnotatedConstructor, true, meta).traversingSuperclasses().find(annotationClass))
                .isNotPresent();
        assertThat(on(notAnnotatedConstructor, true, meta).traversingInterfaces().find(annotationClass)).isPresent();
        assertThat(on(notAnnotatedConstructor, true, meta).traversingSuperclasses().traversingInterfaces().find
                (annotationClass)).isPresent();
    }

    @Test
    public void annotatedField() throws Exception {
        Field annotatedField = NotAnnotatedClass.class.getDeclaredField("annotatedField");
        assertThat(on(annotatedField, false, false).find(FieldAnnotation.class)).isPresent();
        assertThat(on(annotatedField, false, true).find(FieldAnnotation.class)).isPresent();
    }

    @Test
    public void metaAnnotatedField() throws Exception {
        Field metaAnnotatedField = NotAnnotatedClass.class.getDeclaredField("metaAnnotatedField");
        assertThat(on(metaAnnotatedField, false, false).find(FieldAnnotation.class)).isNotPresent();
        assertThat(on(metaAnnotatedField, false, true).find(FieldAnnotation.class)).isPresent();
    }

    @Test
    public void annotatedMethod() throws Exception {
        Method annotatedMethod = NotAnnotatedClass.class.getDeclaredMethod("annotatedMethod");
        assertThat(on(annotatedMethod, false, false).find(MethodAnnotation.class)).isPresent();
        assertThat(on(annotatedMethod, false, true).find(MethodAnnotation.class)).isPresent();
    }

    @Test
    public void metaAnnotatedMethod() throws Exception {
        Method metaAnnotatedMethod = NotAnnotatedClass.class.getDeclaredMethod("metaAnnotatedMethod");
        assertThat(on(metaAnnotatedMethod, false, false).find(MethodAnnotation.class)).isNotPresent();
        assertThat(on(metaAnnotatedMethod, false, true).find(MethodAnnotation.class)).isPresent();
    }

    @Test
    public void annotatedConstructor() throws Exception {
        Constructor annotatedConstructor = NotAnnotatedClass.class.getDeclaredConstructor(String.class);
        assertThat(on(annotatedConstructor, false, false).find(ConstructorAnnotation.class)).isPresent();
        assertThat(on(annotatedConstructor, false, true).find(ConstructorAnnotation.class)).isPresent();
    }

    @Test
    public void metaAnnotatedConstructor() throws Exception {
        Constructor metaAnnotatedConstructor = NotAnnotatedClass.class.getDeclaredConstructor(Integer.class);
        assertThat(on(metaAnnotatedConstructor, false, false).find(ConstructorAnnotation.class)).isNotPresent();
        assertThat(on(metaAnnotatedConstructor, false, true).find(ConstructorAnnotation.class)).isPresent();
    }

    @Test
    public void methodsAnnotatedByInterface() throws Exception {
        Method annotatedMethod = MethodsAnnotatedByInterface.class.getDeclaredMethod("annotatedMethod");
        assertThat(Annotations.on(annotatedMethod).find(MethodAnnotation.class)).isNotPresent();
        assertThat(Annotations.on(annotatedMethod).traversingOverriddenMembers().find(MethodAnnotation.class))
                .isPresent();
    }

    @Test
    public void methodsMetaAnnotatedByInterface() throws Exception {
        Method metaAnnotatedMethod = MethodsAnnotatedByInterface.class.getDeclaredMethod("metaAnnotatedMethod");
        assertThat(Annotations.on(metaAnnotatedMethod).find(MethodAnnotation.class)).isNotPresent();
        assertThat(Annotations.on(metaAnnotatedMethod).traversingOverriddenMembers().includingMetaAnnotations().find
                (MethodAnnotation.class)).isPresent();
    }

    @Test
    public void classAnnotationScopeIsLimited() throws Exception {
        Method notAnnotatedMethod = AnnotatedByInterface.class.getDeclaredMethod("notAnnotatedMethod");
        assertThat(Annotations.on(notAnnotatedMethod).find(TypeAnnotation.class)).isNotPresent();
        assertThat(Annotations.on(notAnnotatedMethod).fallingBackOnClasses().find(TypeAnnotation.class)).isNotPresent();
        assertThat(Annotations.on(notAnnotatedMethod).fallingBackOnClasses().traversingInterfaces().find
                (TypeAnnotation.class)).isPresent();
        assertThat(Annotations.on(notAnnotatedMethod).traversingOverriddenMembers().find(TypeAnnotation.class))
                .isNotPresent();
        assertThat(Annotations.on(notAnnotatedMethod).traversingOverriddenMembers().fallingBackOnClasses().find
                (TypeAnnotation.class)).isPresent();

    }

    private Annotations.OnClass on(AnnotatedElement annotatedElement, boolean fallback, boolean meta) {
        Annotations.OnAnnotatedElement withAnnotatedElement = Annotations.on(annotatedElement);
        Annotations.OnClass OnClass = fallback ? withAnnotatedElement.fallingBackOnClasses() : withAnnotatedElement;
        return meta ? OnClass.includingMetaAnnotations() : OnClass;
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

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    private @interface FieldAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @FieldAnnotation
    private @interface FieldMetaAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
    private @interface MethodAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @MethodAnnotation
    private @interface MethodMetaAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE})
    private @interface ConstructorAnnotation {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.CONSTRUCTOR})
    @ConstructorAnnotation
    private @interface ConstructorMetaAnnotation {
    }

    @TypeAnnotation
    private interface AnnotatedInterface {
        void notAnnotatedMethod();
    }

    @TypeMetaAnnotation
    private interface MetaAnnotatedInterface {
        void notAnnotatedMethod();
    }

    private interface InterfaceWithAnnotatedMethod {
        @MethodAnnotation
        void annotatedMethod();

        @MethodMetaAnnotation
        void metaAnnotatedMethod();
    }

    private static class NotAnnotatedClass {
        Object notAnnotatedField;
        @FieldAnnotation
        Object annotatedField;
        @FieldMetaAnnotation
        Object metaAnnotatedField;

        NotAnnotatedClass() {
        }

        @ConstructorAnnotation
        NotAnnotatedClass(String dummy) {
        }

        @ConstructorMetaAnnotation
        NotAnnotatedClass(Integer dummy) {
        }

        void notAnnotatedMethod() {
        }

        @MethodAnnotation
        void annotatedMethod() {
        }

        @MethodMetaAnnotation
        void metaAnnotatedMethod() {
        }
    }

    @TypeAnnotation
    private static class AnnotatedBaseClass {
        Object notAnnotatedField;

        AnnotatedBaseClass() {
        }

        void notAnnotatedMethod() {
        }
    }

    @TypeMetaAnnotation
    private static class MetaAnnotatedBaseClass {
        Object notAnnotatedField;

        MetaAnnotatedBaseClass() {
        }

        void notAnnotatedMethod() {
        }
    }

    private static class AnnotatedByBaseClass extends AnnotatedBaseClass {
        Object notAnnotatedField;

        AnnotatedByBaseClass() {
        }

        void notAnnotatedMethod() {
        }
    }

    private static class MetaAnnotatedByBaseClass extends MetaAnnotatedBaseClass {
        Object notAnnotatedField;

        MetaAnnotatedByBaseClass() {
        }

        void notAnnotatedMethod() {
        }
    }

    private static class AnnotatedByInterface implements AnnotatedInterface {
        Object notAnnotatedField;

        AnnotatedByInterface() {
        }

        public void notAnnotatedMethod() {
        }

        void notAnnotatedLocalMethod() {
        }
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

    private static class MethodsAnnotatedByInterface implements InterfaceWithAnnotatedMethod {
        @Override
        public void annotatedMethod() {
        }

        @Override
        public void metaAnnotatedMethod() {
        }
    }
}
