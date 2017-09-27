/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.shed.text;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replace ${...} placeholders in a template looking up in a replacement map.
 */
public class TextTemplate {
    private final String template;

    public TextTemplate(String template) {
        this.template = template;
    }

    /**
     * Render the template with the placeholder replaced.
     *
     * @param replacements the replacements to use for placeholders.
     * @return the rendered text.
     */
    public String render(Map<String, Object> replacements) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(template);
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
}
