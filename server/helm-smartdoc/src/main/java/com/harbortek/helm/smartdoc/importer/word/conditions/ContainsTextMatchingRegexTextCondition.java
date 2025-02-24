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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
@ParagraphCondition(id= ConditionTypes.CONTAINS_TEXT_MATCHING_REGEX,hasArgument = true)
public class ContainsTextMatchingRegexTextCondition implements IParagraphCondition {
    public ContainsTextMatchingRegexTextCondition() {
    }

    public boolean isSatisfied(Element paragraph, String argument) {
        String text = WordUtils.plainText(paragraph);
        if (StringUtils.isEmpty(text)) {
            return false;
        } else {
            Pattern regex = Pattern.compile(argument);
            Matcher matcher = regex.matcher(text);
            return matcher.find();
        }
    }
}
