/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.utils.text;

public final class TextUtils {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    private TextUtils() {
    }

    public static String[] splitWords(String text) {
        return text.split(" ");
    }

    public static String wrap(String text, int width) {
        return wrap(text, width, "—");
    }

    public static String wrap(String text, int width, String continuation) {
        StringBuilder sb = new StringBuilder();
        int continuationLength = continuation.length();
        int currentPosition = 0;

        for (String word : splitWords(text)) {
            int wordLength = word.length();

            if (currentPosition + wordLength <= width) {
                if (currentPosition != 0) {
                    sb.append(" ");
                    currentPosition += 1;
                }

                sb.append(word);
                currentPosition += wordLength;
            } else {
                sb.append(LINE_SEPARATOR);
                currentPosition = 0;

                if (wordLength > width) {
                    int i = 0;
                    while (i + width < wordLength) {
                        sb.append(word.substring(i, width - continuationLength)).append(continuation).append(LINE_SEPARATOR);
                        i += width - continuationLength;
                    }
                    String endOfWord = word.substring(i);
                    sb.append(endOfWord);
                    currentPosition = endOfWord.length();
                } else {
                    sb.append(word);
                    currentPosition += wordLength;
                }
            }
        }

        return sb.toString();
    }
}
