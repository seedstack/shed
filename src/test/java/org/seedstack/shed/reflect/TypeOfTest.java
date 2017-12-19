/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.seedstack.shed.reflect;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.StringWriter;
import java.util.List;

/**
 * Unit test for {@link TypeOf}
 */
public class TypeOfTest {

    /**
     * Test method for {@link TypeOf#getType()}.
     */
    @Test
    public void result() {
        TypeOf<List<String>> typeOf = new TypeOf<List<String>>() {
        };
        Assertions.assertThat(typeOf.getType().toString()).isEqualTo("java.util.List<java.lang.String>");
        Assertions.assertThat(typeOf.getRawType()).isEqualTo(List.class);

        TypeOf<Long> typeOf2 = new TypeOf<Long>() {
        };
        Assertions.assertThat(typeOf2.getType()).isEqualTo(Long.class);
        Assertions.assertThat(typeOf2.getRawType()).isEqualTo(Long.class);

    }

    /**
     * Test method for {@link TypeOf#getType()}.
     */
    @SuppressWarnings("rawtypes")
    @Test
    public void getTypeWithoutParameterized() {
        StringWriter stringWriter = new StringWriter();
        try {
            new TypeOf() {
            };
            Assertions.fail("Should throw a SeedException");
        } catch (Exception e) {
            Assertions.assertThat(e.getMessage()).contains("Missing generic parameter");
        }
    }
}
