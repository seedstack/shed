/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.lang;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassLoadersTest {
    @Test
    public void findMostCompleteClassLoader() throws Exception {
        assertThat(ClassLoaders.findMostCompleteClassLoader(ClassLoadersTest.class)).isNotNull();
        assertThat(ClassLoaders.findMostCompleteClassLoader(null)).isNotNull();
    }
}