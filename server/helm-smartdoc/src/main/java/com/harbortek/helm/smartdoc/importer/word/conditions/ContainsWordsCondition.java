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

package com.harbortek.helm.smartdoc.importer.word.conditions;

import com.harbortek.helm.smartdoc.constants.ConditionTypes;
import com.harbortek.helm.smartdoc.utils.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import java.util.regex.Pattern;
@ParagraphCondition(id= ConditionTypes.CONTAINS_WORDS,hasArgument = true)
public class ContainsWordsCondition implements IParagraphCondition {
    private boolean caseSensitive=false;

    public ContainsWordsCondition() {
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isSatisfied(Element paragraph,String argument) {
        String text = WordUtils.plainText(paragraph);
        return StringUtils.isNotEmpty(text) && this.containsWords(text, argument);
    }

    private boolean containsWords(String text, String arg) {
        if (!this.caseSensitive) {
            text = text.toLowerCase();
            arg = arg.toLowerCase();
        }

        String[] words = arg.split(",");
        int var6 = words.length;

        for (String s : words) {
            String word = s;
            word = word.trim();
            if (!word.isEmpty()) {
                Pattern pattern = Pattern.compile("\\b\\Q" + word + "\\E\\b");
                if (pattern.matcher(text).find()) {
                    return true;
                }
            }
        }

        return false;
    }
}
