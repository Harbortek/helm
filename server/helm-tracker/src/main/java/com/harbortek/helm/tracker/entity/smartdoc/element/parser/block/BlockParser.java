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

import com.harbortek.helm.tracker.entity.block.*;

import com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po.HtmlParser;
import com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po.ParserRegister;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateDescendant;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateText;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.header.*;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.paragraph.ParagraphHtmlParserConf;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.paragraph.ParagraphSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.title.TitleSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.trackerItem.TrackerItemSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.texts.style.DefaultParserStyleHtmlFn;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.List;

public class BlockParser {
    static {
        ParserRegister.registerParseStyleHtmlHandler(new DefaultParserStyleHtmlFn());
        ParserRegister.registerParseElemHtmlConf(new ParagraphHtmlParserConf());
    }

    public static SlateNode parse(DocBlock docBlock) {
        DocBlockData blockData = docBlock.getData();
        if (blockData instanceof TitleBlockData) {
            TitleSlateElement<SlateDescendant> titleSlateElement = new TitleSlateElement<>();
            List<SlateDescendant> children = new ArrayList<>();
            for (Node node : Jsoup.parse(blockData.getText()).body().childNodes()) {
                Element element = wrapperText(node);
                SlateDescendant parseElemHtml = HtmlParser.parseElemHtml(element);
                children.add(parseElemHtml);
            }
            titleSlateElement.setChildren(children);
            return titleSlateElement;
        } else if (blockData instanceof ParagraphBlockData) {
            ParagraphBlockData paragraphBlockData = (ParagraphBlockData) blockData;
            ParagraphSlateElement<SlateDescendant> paragraphSlateElement = new ParagraphSlateElement<>();
            List<SlateDescendant> children = new ArrayList<>();
            for (Node node : Jsoup.parse(paragraphBlockData.getText()).body().childNodes()) {
                Element element = wrapperText(node);
                SlateDescendant parseElemHtml = HtmlParser.parseElemHtml(element);
                children.add(parseElemHtml);
            }
            paragraphSlateElement.setChildren(children);
            return paragraphSlateElement;
        } else if (blockData instanceof TrackerItemBlockData) {
            TrackerItemBlockData trackerItemBlockData = (TrackerItemBlockData) blockData;
            TrackerItemSlateElement<SlateDescendant> trackerItemSlateElement = new TrackerItemSlateElement<>();
            List<SlateDescendant> children = new ArrayList<>();
            trackerItemSlateElement.setRef(trackerItemBlockData.getRefId().toString());
            for (Node node : Jsoup.parse(trackerItemBlockData.getName()).body().childNodes()) {
                TrackerItemSlateElement.TrackerItemTitleSlateElement trackerItemTitleSlateElement = new TrackerItemSlateElement.TrackerItemTitleSlateElement();
                children.add(trackerItemTitleSlateElement);
                List<SlateDescendant> subChildren = new ArrayList<>();
                Element element = wrapperText(node);
                SlateDescendant parseElemHtml = HtmlParser.parseElemHtml(element);
                subChildren.add(parseElemHtml);
                trackerItemTitleSlateElement.setChildren(subChildren);
                trackerItemTitleSlateElement.setRef(trackerItemBlockData.getRefId().toString());
            }
            TrackerItemSlateElement.TrackerItemDescriptionSlateElement trackerItemDescriptionSlateElement = new TrackerItemSlateElement.TrackerItemDescriptionSlateElement();
            children.add(trackerItemDescriptionSlateElement);
            List<SlateDescendant> subChildren = new ArrayList<>();
            trackerItemDescriptionSlateElement.setChildren(subChildren);
            trackerItemDescriptionSlateElement.setRef(trackerItemBlockData.getRefId().toString());

            Element container = new Element("span");
            for (Node node : Jsoup.parse(trackerItemBlockData.getText()).body().childNodes()) {
                Element element = wrapperText(node);
                container.appendChild(element);
            }
            SlateDescendant parseElemHtml = HtmlParser.parseElemHtml(container);
            subChildren.add(parseElemHtml);
            trackerItemSlateElement.setChildren(children);


            TrackerItemSlateElement.TrackerItemExtraSlateElement trackerItemExtraSlateElement = new TrackerItemSlateElement.TrackerItemExtraSlateElement();
            children.add(trackerItemExtraSlateElement);
            List<SlateDescendant> subChildren2 = new ArrayList<>();
//            ParagraphSlateElement p = new ParagraphSlateElement<SlateDescendant>();
            SlateText text = new SlateText("");
//            p.getChildren().add(text);
            subChildren2.add(text);
            trackerItemExtraSlateElement.setChildren(subChildren2);
            trackerItemExtraSlateElement.setRef(trackerItemBlockData.getRefId().toString());
            return trackerItemSlateElement;
        } else if (blockData instanceof HeaderBlockData) {
            HeaderBlockData headerBlockData = (HeaderBlockData) blockData;
            HeaderSlateElement headerSlateElement = null;
            if (headerBlockData.getLevel() == 1L) {
                headerSlateElement = new Header1SlateElement();
            } else if (headerBlockData.getLevel() == 2L) {
                headerSlateElement = new Header2SlateElement();
            } else if (headerBlockData.getLevel() == 3L) {
                headerSlateElement = new Header3SlateElement();
            } else if (headerBlockData.getLevel() == 4L) {
                headerSlateElement = new Header4SlateElement();
            } else if (headerBlockData.getLevel() == 5L) {
                headerSlateElement = new Header5SlateElement();
            } else {
                headerSlateElement = new Header1SlateElement();
            }
            List<SlateDescendant> children = new ArrayList<>();
            for (Node node : Jsoup.parse(headerBlockData.getText()).body().childNodes()) {
                Element element = wrapperText(node);
                SlateDescendant parseElemHtml = HtmlParser.parseElemHtml(element);
                children.add(parseElemHtml);
            }
            headerSlateElement.setChildren(children);
            return headerSlateElement;
        } else {
            return null;
        }
    }

    private static Element wrapperText(Node textNode) {
        if (textNode instanceof TextNode) {
            Element wrapper = new Element("span");
            wrapper.appendChild(textNode);
            return wrapper;
        } else {
            return (Element) textNode;
        }
    }
}
