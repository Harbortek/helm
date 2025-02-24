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

package com.harbortek.helm.tracker.entity.smartdoc.element.po.texts.style;

import com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po.ParseStyleHtmlFn;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateDescendant;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateText;
import org.jsoup.nodes.Element;

public class DefaultParserStyleHtmlFn implements ParseStyleHtmlFn {
    public static boolean isMatch(Element text, String selector) {
        if (text == null) {
            return false;
        }
        if (text.is(selector)) {
            return true;
        }
        return false;
    }

    @Override
    public SlateDescendant apply(Element elem, SlateDescendant textNode) {
        if (!(textNode instanceof SlateText)) return textNode;
        SlateText text = new SlateText();
        text.setText(((SlateText) textNode).getText());
        // bold
        if (isMatch(elem, "b,strong")) {
            text.setBold(true);
        }

        // italic
        if (isMatch(elem, "i,em")) {
            text.setItalic(true);
        }

        // underline
        if (isMatch(elem, "u")) {
            text.setUnderline(true);
        }

        // through
        if (isMatch(elem, "s,strike")) {
            text.setThrough(true);
        }

        // sub
        if (isMatch(elem, "sub")) {
            text.setSub(true);
        }

        // sup
        if (isMatch(elem, "sup")) {
            text.setSub(true);
        }

        // code
        if (isMatch(elem, "code")) {
            text.setCode(true);
        }

        return text;
    }
}
