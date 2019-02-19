/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class ClassesTest {
    private Field baseClassField;
    private Field subClassField;
    private Method subClassFromInterfaceMethod;
    private Method subClassInheritedMethod;
    private Method baseClassFromInterfaceMethod;
    private Method someInterfaceMethod;
    private Method someOtherInterfaceMethod;

    @Before
    public void setUp() throws Exception {
        baseClassField = BaseClass.class.getDeclaredField("baseClassField");
        subClassField = SubClass.class.getDeclaredField("subClassField");
        subClassFromInterfaceMethod = SubClass.class.getDeclaredMethod("someOtherInterfaceMethod");
        subClassInheritedMethod = SubClass.class.getDeclaredMethod("someInterfaceMethod");
        baseClassFromInterfaceMethod = BaseClass.class.getDeclaredMethod("someInterfaceMethod");
        someInterfaceMethod = SomeInterface.class.getDeclaredMethod("someInterfaceMethod");
        someOtherInterfaceMethod = SomeOtherInterface.class.getDeclaredMethod("someOtherInterfaceMethod");
    }

    @Test
    public void findField() throws Exception {
        assertThat(Classes.from(SubClass.class).fields().filter(field -> "subClassField".equals(field.getName()))
                .findFirst()).isEqualTo(Optional.of(subClassField));
        assertThat(Classes.from(SubClass.class).fields().filter(field -> "baseClassField".equals(field.getName()))
                .findFirst()).isEmpty();
        assertThat(Classes.from(SubClass.class).traversingSuperclasses().fields().filter(field -> "baseClassField"
                .equals(field.getName())).findFirst()).isEqualTo(Optional.of(baseClassField));
    }

    @Test
    public void fieldOrderIsBottomUp() throws Exception {
        assertThat(Classes.from(SubClass.class).traversingSuperclasses().fields()).containsSubsequence(subClassField,
                baseClassField);
    }

    @Test
    public void methodOrderIsBottomUp() throws Exception {
        List<Method> result = Classes.from(SubClass.class)
                .traversingSuperclasses()
                .traversingInterfaces()
                .methods()
                .collect(toList());
        assertThat(result).containsSubsequence(subClassInheritedMethod, someOtherInterfaceMethod,
                baseClassFromInterfaceMethod, someInterfaceMethod);
        assertThat(result).containsSubsequence(subClassFromInterfaceMethod, someOtherInterfaceMethod,
                baseClassFromInterfaceMethod, someInterfaceMethod);
    }

    @Test
    public void doesNotGoDownHierarchy() throws Exception {
        assertThat(Classes.from(SubClass.class).traversingSuperclasses().traversingInterfaces().fields().filter(field
                -> "subClassField".equals(field.getName())).findFirst()).isEqualTo(Optional.of(subClassField));
        assertThat(Classes.from(BaseClass.class).traversingSuperclasses().traversingInterfaces().fields().filter
                (field -> "subClassField".equals(field.getName())).findFirst()).isEmpty();
    }

    @Test
    public void instantiatePrimitiveTypes() throws Exception {
        assertThat(Classes.instantiateDefault(int.class)).isEqualTo(0);
        assertThat(Classes.instantiateDefault(boolean.class)).isFalse();
        assertThat(Classes.instantiateDefault(int.class)).isEqualTo(0);
        assertThat(Classes.instantiateDefault(long.class)).isEqualTo(0L);
        assertThat(Classes.instantiateDefault(short.class)).isEqualTo((short) 0);
        assertThat(Classes.instantiateDefault(float.class)).isEqualTo(0f);
        assertThat(Classes.instantiateDefault(double.class)).isEqualTo(0d);
        assertThat(Classes.instantiateDefault(byte.class)).isEqualTo((byte) 0);
        assertThat(Classes.instantiateDefault(char.class)).isEqualTo((char) 0);
    }

    @Test
    public void instantiateBoxedTypes() throws Exception {
        assertThat(Classes.instantiateDefault(Integer.class)).isEqualTo(0);
        assertThat(Classes.instantiateDefault(Boolean.class)).isFalse();
        assertThat(Classes.instantiateDefault(Integer.class)).isEqualTo(0);
        assertThat(Classes.instantiateDefault(Long.class)).isEqualTo(0L);
        assertThat(Classes.instantiateDefault(Short.class)).isEqualTo((short) 0);
        assertThat(Classes.instantiateDefault(Float.class)).isEqualTo(0f);
        assertThat(Classes.instantiateDefault(Double.class)).isEqualTo(0d);
        assertThat(Classes.instantiateDefault(Byte.class)).isEqualTo((byte) 0);
        assertThat(Classes.instantiateDefault(Character.class)).isEqualTo((char) 0);
    }

    @Test
    public void instantiateArrays() throws Exception {
        assertThat(Classes.instantiateDefault(Integer[].class)).isEqualTo(new Integer[0]);
        assertThat(Classes.instantiateDefault(int[].class)).isEqualTo(new int[0]);
        assertThat(Classes.instantiateDefault(String[].class)).isEqualTo(new String[0]);
    }

    interface SomeInterface {
        void someInterfaceMethod();
    }

    interface SomeOtherInterface {
        void someOtherInterfaceMethod();
    }

    static class BaseClass implements SomeInterface {
        private String baseClassField;

        public void someInterfaceMethod() {
        }
    }

    static class SubClass extends BaseClass implements SomeOtherInterface {
        private String subClassField;

        public void someInterfaceMethod() {
        }

        public void someOtherInterfaceMethod() {
        }
    }
}
