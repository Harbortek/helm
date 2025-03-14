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

package com.harbortek.helm.tracker.entity.smartdoc.element.parser.block;

import com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po.ParseStyleHtmlFn;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.style.StyleedStyle;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.text.SlateText;
import org.jsoup.nodes.Element;

import java.util.List;

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
    public SlateNode apply(Element elem, SlateNode textNode) {
        if (!(textNode instanceof SlateText)) return textNode;
        SlateText text = new SlateText();
        text.setText(((SlateText) textNode).getText());
        // bold
        StyleedStyle style = StyleedStyle.builder().build();
        boolean styleChanged = false;
        if (isMatch(elem, "b,strong")) {
            style.setBold(true);
            styleChanged = true;
        }
        // italic
        if (isMatch(elem, "i,em")) {
            style.setItalic(true);
            styleChanged = true;
        }

        // underline
        if (isMatch(elem, "u")) {
            style.setUnderline(true);
            styleChanged = true;
        }

        // through
        if (isMatch(elem, "s,strike")) {
            style.setThrough(true);
            styleChanged = true;
        }

        // sub
        if (isMatch(elem, "sub")) {
            style.setSub(true);
            styleChanged = true;
        }

        // sup
        if (isMatch(elem, "sup")) {
            style.setSub(true);
            styleChanged = true;
        }

        // code
        if (isMatch(elem, "code")) {
            style.setCode(true);
            styleChanged = true;
        }
        // through
        if (isMatch(elem, "s,strike")) {
            style.setThrough(true);
            styleChanged = true;
        }
        if (styleChanged) {
            text.setStyles(List.of(style));
        }
        return text;
    }
}