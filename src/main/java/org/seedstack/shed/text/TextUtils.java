/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.text;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtils {
    private static final String LINE_SEPARATOR = "\n";
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

    /**
     * Wrap text to the specified width using a greedy algorithm. The continuation symbol "—" is used for long words.
     *
     * @param text  the text to wrap.
     * @param width the maximum width of a line.
     * @return the wrapped text.
     */
    public static String wrap(String text, int width) {
        return wrap(text, width, "—");
    }

    /**
     * Wrap text to the specified width using a greedy algorithm. The continuation symbol is used when
     * a long word doesn't fit on one line and must be broken apart.
     *
     * @param text         the text to wrap.
     * @param width        the maximum width of a line.
     * @param continuation the continuation symbol in case a word is too long for a line.
     * @return the wrapped text.
     */
    public static String wrap(String text, int width, String continuation) {
        StringBuilder sb = new StringBuilder();
        int continuationLength = continuation.length();
        int currentPosition = 0;

        for (String word : splitWords(text)) {
            String lastWord;
            int wordLength = word.length();

            if (currentPosition + wordLength <= width) {
                if (currentPosition != 0) {
                    sb.append(" ");
                    currentPosition += 1;
                }

                sb.append(lastWord = word);
                currentPosition += wordLength;
            } else {
                if (currentPosition > 0) {
                    sb.append(LINE_SEPARATOR);
                    currentPosition = 0;
                }

                if (wordLength > width) {
                    int i = 0;
                    while (i + width < wordLength) {
                        sb.append(word.substring(i, width - continuationLength)).append(continuation).append(LINE_SEPARATOR);
                        i += width - continuationLength;
                    }
                    String endOfWord = word.substring(i);
                    sb.append(lastWord = endOfWord);
                    currentPosition = endOfWord.length();
                } else {
                    sb.append(lastWord = word);
                    currentPosition += wordLength;
                }
            }

            int lastNewLine = lastWord.lastIndexOf("\n");
            if (lastNewLine != -1) {
                currentPosition = lastWord.length() - lastNewLine;
            }
        }

        return sb.toString();
    }

    /**
     * Replace ${...} placeholders in a string looking up in a replacement map.
     *
     * @param text         the text to replace.
     * @param replacements the map of replacements.
     * @return the replaced text.
     */
    public static String replaceTokens(String text, Map<String, Object> replacements) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            Object replacement = replacements.get(matcher.group(1));
            matcher.appendReplacement(buffer, "");

            if (replacement != null) {
                buffer.append(replacement.toString());
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
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
