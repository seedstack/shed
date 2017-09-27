/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.seedstack.shed.reflect.ReflectUtils.*;

public class ReflectUtilsTest {
    private String simpleField;

    @Test
    public void testMakeAccessible() throws Exception {
        makeAccessible(ReflectUtilsTest.class.getDeclaredField("simpleField"));
    }

    @Test
    public void testSetValue() throws Exception {
        setValue(makeAccessible(ReflectUtilsTest.class.getDeclaredField("simpleField")), this, "test");
        assertThat(simpleField).isEqualTo("test");
    }

    @Test
    public void testGetValue() throws Exception {
        simpleField = "someValue";
        assertThat((String) getValue(makeAccessible(ReflectUtilsTest.class.getDeclaredField("simpleField")), this))
                .isEqualTo("someValue");
    }

    @Test
    public void testInvoke() throws Exception {
        assertThat((String) invoke(makeAccessible(ReflectUtilsTest.class.getDeclaredMethod("someMethod", String
                .class, String.class)), this, "a", "b")).isEqualTo("ab");
    }

    private String someMethod(String arg1, String arg2) {
        return arg1 + arg2;
    }
}