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

package com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.paragraph;

import com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po.ParseElemHtmlConf;
import com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po.ParseElemHtmlFn;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateDescendant;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateText;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class ParagraphHtmlParserConf extends ParseElemHtmlConf {
    private static final String SELECTOR = "p:not([data-w-e-type])";

    public ParagraphHtmlParserConf() {
        super(SELECTOR, new ParagraphHtmlParserFn());
    }

    private static class ParagraphHtmlParserFn implements ParseElemHtmlFn {
        public static String SELECTOR = "p:not([data-w-e-type])";

        @Override
        public SlateElement apply(Element elem, List<SlateDescendant> children) {

//            children = children.stream().filter(child -> {
//                if (Tool.isSingleText(child)) return true;
//                if (Tool.isInline(child)) return true;
//                return false;
//            }).toList();

            // 无 children ，则用纯文本
            if (children.size() == 0) {
                children = new ArrayList<>();
                children.add(new SlateText(elem.text()));
            }
            ParagraphSlateElement paragraphSlateElement = new ParagraphSlateElement<>();
            paragraphSlateElement.setChildren(children);
//        return  new SlateElement[]{paragraphSlateElement};
            return paragraphSlateElement;
        }

    }


}
