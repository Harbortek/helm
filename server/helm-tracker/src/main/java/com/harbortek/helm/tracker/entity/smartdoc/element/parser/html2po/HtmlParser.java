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

package com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po;

import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateDescendant;
import org.jsoup.nodes.Element;

import java.util.Arrays;

public class HtmlParser {
    /**
     * Process DOM Element HTML
     *
     * @param elem   DOM element
     * @return Descendant array
     */
    public static SlateDescendant parseElemHtml(Element elem) {
        // Pre-parse
        for (PreParseHtmlConf conf : ParserRegister.PRE_PARSE_HTML_CONF_LIST) {
            if (elem instanceof Element) {
                if (elem.is(conf.getSelector())) {
                    elem = conf.getPreParseHtml().apply(elem);
                }
            }
        }

        String tagName = elem.nodeName().toLowerCase();

        // Handle <span> - check for data-w-e-type attribute
        if (tagName.equals("span")) {
            if (elem.hasAttr("data-w-e-type")) {
                return ParseCommonElemHtml.parseCommonElemHtml(elem);
            } else {
                return ParseTextElemHtml.parseTextElemHtml(elem);
            }
        }

        // Special handling for <code>
        if (tagName.equals("code")) {
            String parentTagName = ((Element) elem).parent().tagName().toLowerCase();
            if (parentTagName.equals("pre")) {
                // <code> inside <pre> is treated as element
                return ParseCommonElemHtml.parseCommonElemHtml(((Element) elem));
            } else {
                // <code> not in <pre> is treated as text
                return ParseTextElemHtml.parseTextElemHtml(elem);
            }
        }

        // Normal processing
        if (Arrays.asList(ParserRegister.TEXT_TAGS).contains(tagName)) {
            // Text node
            return ParseTextElemHtml.parseTextElemHtml(elem);
        } else {
            // Element node
            return ParseCommonElemHtml.parseCommonElemHtml(elem);
        }
    }
}
