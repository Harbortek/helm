/*
 * Copyright [2025] [Harbortek Corp.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harbortek.helm.smartdoc.importer.word.extractors;

import com.harbortek.helm.smartdoc.constants.ValueExtractorTypes;
import com.harbortek.helm.smartdoc.utils.WordUtils;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ValueExtractor(id= ValueExtractorTypes.RICH_TEXT_WITHOUT_REGEX,hasArgument = true)
public class RichTextWithoutRegexExtractor implements IValueExtractor {
    @Override
    public Object extractValue(Element paragraph, String argument) {
        Pattern regex = Pattern.compile(argument);
        String html = WordUtils.html(paragraph);

        return removeMatchingText(html,regex);
    }

    public String removeMatchingText(String html, Pattern regex) {
        String plainText = html;
        Matcher matcher = regex.matcher(plainText);
        if (matcher.find()) {
            String matched = matcher.groupCount() > 0 ? matcher.group(1) : matcher.group();
            html = removeTextFromHtml(matched, html);
        }

        return html;
    }
    public static String removeTextFromHtml(String text, String html) {
        StringBuilder s = new StringBuilder();

        for(int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (i > 0) {
                s.append("(<[^<>]*>)*");
            }

            s.append("(\\Q");
            s.append(c);
            s.append("\\E)");
        }

        Pattern pattern = Pattern.compile(s.toString());
        Matcher matcher = pattern.matcher(html);
        if (!matcher.find()) {
            return html;
        } else {
            StringBuilder result = new StringBuilder(html);

            for(int i = text.length() - 1; i >= 0; --i) {
                int groupIdx = 2 * i + 1;
                result.replace(matcher.start(groupIdx), matcher.end(groupIdx), "");
            }

            return result.toString();
        }
    }
}
