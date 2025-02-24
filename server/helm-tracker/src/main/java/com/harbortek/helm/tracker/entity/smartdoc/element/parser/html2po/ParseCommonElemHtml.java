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

import com.harbortek.helm.tracker.entity.smartdoc.element.parser.block.Helper;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateDescendant;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateText;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.paragraph.ParagraphSlateElement;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangfupeng
 * @description parse elem html
 */
public class ParseCommonElemHtml {
    /**
     * Try to insert text to the last item of children if it's a text node.
     *
     * @param children children
     * @param str      text to insert
     * @return whether the insertion was successful
     */
    private static boolean tryInsertTextToChildrenLastItem(List<SlateDescendant> children, String str) {
        int len = children.size();
        if (len > 0) {
            SlateDescendant lastItem = children.get(len - 1);
            if (lastItem instanceof SlateText) {
                SlateText textNode = (SlateText) lastItem;
                if (ParserRegister.isSingleText(textNode)) {
                    textNode.setText(textNode.getText() + str);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Generate slate node children.
     *
     * @param elem   DOM element
     * @return list of SlateDescendant
     */
    public static List<SlateDescendant> genChildren(Node elem) {
        List<SlateDescendant> children = new ArrayList<>();

        // Check if void node
        if (elem.hasAttr("data-w-e-is-void")) {
            return children;
        }

        List<Node> childNodes = elem.childNodes();

        // Handle empty line (only one child, which is <br>)
        if (childNodes.size() == 1 && childNodes.get(0).nodeName().equals("BR")) {
            children.add(new SlateText(""));
            return children;
        }

        // Process child nodes
        for (Node child : childNodes) {
            if (child instanceof Element) {
                if (child.nodeName().equals("BR")) {
                    boolean inserted = tryInsertTextToChildrenLastItem(children, "\n");
                    if (!inserted) {
                        children.add(new SlateText("\n"));
                    }
                    continue;
                }

                SlateDescendant parsedRes = HtmlParser.parseElemHtml((Element) child);
                children.add(parsedRes);
            } else if (child instanceof TextNode) {
                String text = ((TextNode) child).text();
                if (text != null) {
                    if (text.trim().isEmpty() && text.contains("\n")) {
                        continue;
                    }

                    text = Helper.replaceSpace160(text);
                    boolean inserted = tryInsertTextToChildrenLastItem(children, text);
                    if (!inserted) {
                        children.add(new SlateText(text));
                    }
                }
            }
        }
        return children;
    }

    /**
     * Default parser for parseElemHtml, directly converts to paragraph.
     *
     * @param elem     DOM element
     * @param children children
     * @return Element
     */
    private static SlateElement defaultParser(Element elem, List<SlateDescendant> children) {
        SlateText text = new SlateText(elem.text().replaceAll("\\s+", " "));
        ParagraphSlateElement<SlateText> paragraph = new ParagraphSlateElement<SlateText>();
        List<SlateText> childrenText = new ArrayList<>();
        childrenText.add(text);
        paragraph.setChildren(childrenText);
        return paragraph;
    }

    /**
     * Get the current HTML element's parseElemHtml function.
     *
     * @param elem DOM element
     * @return ParseElemHtmlFn
     */
    private static ParseElemHtmlFn getParser(Element elem) {
        for (String selector : ParserRegister.PARSE_ELEM_HTML_CONF.keySet()) {
            if (elem.is(selector)) {
                return ParserRegister.PARSE_ELEM_HTML_CONF.get(selector);
            }
        }
        return new ParseElemHtmlFn() {
            @Override
            public SlateElement apply(Element elem, List<SlateDescendant> children) {
                return defaultParser(elem, children);
            }
        };
    }

    /**
     * Process common DOM element HTML, non-text elements like span or font.
     *
     * @param elem   DOM element
     * @return list of Element
     */
    public static SlateElement parseCommonElemHtml(Element elem) {
        List<SlateDescendant> children = ParseCommonElemHtml.genChildren(elem);
        ParseElemHtmlFn parser = getParser(elem);
        SlateElement element = parser.apply(elem, children);

        if (!ParserRegister.isVoid(element)) {
            if (children.isEmpty()) {
                element.setChildren(new ArrayList());
                element.getChildren().add(new SlateText(elem.html().replaceAll("\\s+", " ")));
            }

            // Process style
            for (ParseStyleHtmlFn fn : ParserRegister.PARSE_STYLE_HTML_FN_LIST) {
                element = (SlateElement) fn.apply(elem, element);
            }
        }
        return element;
    }
}
