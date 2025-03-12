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

package com.harbortek.helm.tracker.entity.smartdoc.element.po.element.list;

import com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po.ParseElemHtmlConf;
import com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po.ParseElemHtmlFn;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.SlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.paragraph.ParagraphSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.text.SlateText;
import org.jsoup.nodes.Element;

import java.util.List;

public class ListHtmlParserConf extends ParseElemHtmlConf {
    private static final String SELECTOR = "ul,ol";

    public ListHtmlParserConf() {
        super(SELECTOR, new ListHtmlParserFn());
    }

    private static class ListHtmlParserFn implements ParseElemHtmlFn {
        public static String SELECTOR = "ul,ol";

        @Override
        public SlateElement apply(Element elem, List<SlateNode> children) {

//            children = children.stream().filter(child -> {
//                if (Tool.isSingleText(child)) return true;
//                if (Tool.isInline(child)) return true;
//                return false;
//            }).toList();
            String tagName = elem.tagName();

            // 无 children ，则用纯文本
            if (children.size() == 0) {
                children.add(new SlateText(elem.text()));
            }
            ListSlateElement listSlateElement = new ListSlateElement<>();
            listSlateElement.setOrdered(tagName.equals("ol"));
            listSlateElement.setChildren(children);
//        return  new SlateElement[]{paragraphSlateElement};
            return listSlateElement;
        }

    }


}
