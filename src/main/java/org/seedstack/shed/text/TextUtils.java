/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtils {
    private static final Pattern LINE_START_PATTERN = Pattern.compile("^.*", Pattern.MULTILINE);

    private TextUtils() {
    }

    /**
     * Split a text into an array of words using space (" ") as separator.
     *
     * @param text the text to split.
     * @return an array of each word.
     */
    public static String[] splitWords(String text) {
        return text.split(" ");
    }

    public static String leftPad(String text, String padding, int linesToIgnore) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = LINE_START_PATTERN.matcher(text);

        while (matcher.find()) {
            if (linesToIgnore > 0) {
                linesToIgnore--;
            } else {
                result.append(padding);
            }
            result.append(matcher.group()).append("\n");
        }
        return result.toString();
    }
}
