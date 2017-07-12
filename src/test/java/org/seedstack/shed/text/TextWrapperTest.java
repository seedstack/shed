/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.text;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TextWrapperTest {
    @Test
    public void simpleWrap() throws Exception {
        String longString = "Nec vox accusatoris ulla licet subditicii in his malorum quaerebatur acervis ut saltem specie tenus crimina praescriptis legum committerentur, quod aliquotiens fecere principes saevi: sed quicquid Caesaris implacabilitati sedisset, id velut fas iusque perpensum confestim urgebatur impleri.";
        String wrapped = new TextWrapper().wrap(longString);
        for (String line : wrapped.split("\n")) {
            assertThat(line.length()).isLessThanOrEqualTo(120);
        }
    }

    @Test
    public void wrapPreserveShortStrings() throws Exception {
        String stringWithNewLine = "Nec vox accusatoris\n";
        assertThat(new TextWrapper().wrap(stringWithNewLine)).isEqualTo(stringWithNewLine);
        String stringWithoutNewLine = "Nec vox accusatoris";
        assertThat(new TextWrapper().wrap(stringWithoutNewLine)).isEqualTo(stringWithoutNewLine);
    }

    @Test
    public void longWordWrapStrictMode() throws Exception {
        String longWord = "abcdefghijklmnopqrstuvwxyz----abcdefghijklmnopqrstuvwxyz----abcdefghijklmnopqrstuvwxyz----abcdefghijklmnopqrstuvwxyz----abcdefghijklmnopqrstuvwxyz----";
        String wrapped = new TextWrapper(TextWrapper.DEFAULT_WIDTH, TextWrapper.DEFAULT_CONTINUATION, true).wrap(longWord);
        for (String line : wrapped.split("\n")) {
            assertThat(line.length()).isLessThanOrEqualTo(80);
        }
        assertThat(wrapped).contains("—");
    }

    @Test
    public void longWordWrapLaxMode() throws Exception {
        String longWord = "abcdefghijklmnopqrstuvwxyz----abcdefghijklmnopqrstuvwxyz----abcdefghijklmnopqrstuvwxyz----abcdefghijklmnopqrstuvwxyz----abcdefghijklmnopqrstuvwxyz----";
        String wrapped = new TextWrapper().wrap(longWord);
        for (String line : wrapped.split("\n")) {
            assertThat(line.length()).isEqualTo(longWord.length());
        }
        assertThat(wrapped).doesNotContain("—");
    }
}