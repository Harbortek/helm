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

import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HtmlUtil;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.entity.block.*;

import com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po.HtmlParser;
import com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po.ParserRegister;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.image.ImageHtmlParserConf;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.list.ListHtmlParserConf;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.list.ListSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.paragraph.ParagraphHtmlParserConf;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.table.TableHtmlParserConf;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.table.TableSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.text.SlateText;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.header.*;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.paragraph.ParagraphSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.title.TitleSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.trackerItem.TrackerItemSlateElement;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Block2Node {
    static {
        ParserRegister.registerParseStyleHtmlHandler(new DefaultParserStyleHtmlFn());
        ParserRegister.registerParseElemHtmlConf(new ParagraphHtmlParserConf());
        ParserRegister.registerParseElemHtmlConf(new ListHtmlParserConf());
        ParserRegister.registerParseElemHtmlConf(new ImageHtmlParserConf());
        ParserRegister.registerParseElemHtmlConf(new TableHtmlParserConf());
    }

    public static SlateNode parse(DocBlock docBlock) {
        DocBlockData blockData = docBlock.getData();
        if (blockData instanceof TitleBlockData) {
            TitleSlateElement<SlateNode> titleSlateElement = new TitleSlateElement<>();
            List<SlateNode> children = new ArrayList<>();

            for (Node node : Jsoup.parse(blockData.getText()).body().childNodes()) {
                Element element = wrapperText(node);
                SlateNode parseElemHtml = HtmlParser.parseElemHtml(element);
                children.add(parseElemHtml);
            }
            titleSlateElement.setChildren(children);
            titleSlateElement.setId(docBlock.getId());
            return titleSlateElement;
        } else if (blockData instanceof ParagraphBlockData) {
            ParagraphBlockData paragraphBlockData = (ParagraphBlockData) blockData;
            List<SlateNode> children = new ArrayList<>();
            List<Node> all = Jsoup.parse(paragraphBlockData.getText()).body().childNodes();
            if (all.isEmpty()) {
                ParagraphSlateElement<SlateNode> paragraphSlateElement = new ParagraphSlateElement<>();
                SlateText slateText = new SlateText();
                paragraphSlateElement.getChildren().add(slateText);
                return paragraphSlateElement;
            }
            if (all.get(0) instanceof Element) {
                //ul,ol 特殊处理
                if (((Element) all.get(0)).is("ul,ol")) {
                    Element firstChild = (Element) all.get(0);
                    String tagName = firstChild.tagName();
                    ParagraphSlateElement paragraphSlateElement = new ParagraphSlateElement();
                    for (Node node : firstChild.childNodes()) {
                        Element element = wrapperText(node);
                        if (StringUtils.isEmpty(StringUtils.trimToEmpty(element.html()))) {
                            continue;
                        }
                        SlateNode parseElemHtml = HtmlParser.parseElemHtml(element);
                        ListSlateElement listSlateElement = new ListSlateElement<>();
                        listSlateElement.setLevel(1L);
                        listSlateElement.setOrdered(tagName.equals("ol"));
                        listSlateElement.getChildren().add(parseElemHtml);
                        children.add(listSlateElement);
                    }
                    paragraphSlateElement.setChildren(children);
                    paragraphSlateElement.setId(docBlock.getId());
                    return paragraphSlateElement;
                }
                //table 特殊处理
                if (((Element) all.get(0)).is("table,tbody")) {
                    Element firstChild = (Element) all.get(0);
                    final List<Element> trElements = new ArrayList<>();
                    firstChild.getElementsByTag("tr").forEach(e -> trElements.add(e));
                    TableSlateElement tableSlateElement = new TableSlateElement<>();
                    for (Element tr : trElements) {
                        boolean isHeader = tr.tagName().equals("th");
                        TableSlateElement.TableRowSlateElement tableRowSlateElement = new TableSlateElement.TableRowSlateElement();
                        List<Element> tdElements = new ArrayList<>();
                        for (Element td : tr.getElementsByTag("td")) {
                            tdElements.add(td);
                        }
                        for (Element td : tr.getElementsByTag("th")) {
                            tdElements.add(td);
                        }
                        for (Element td : tdElements) {
                            String colSpan = td.attr("colspan");
                            String rowspan = td.attr("rowspan");
                            TableSlateElement.TableCellSlateElement tableCellSlateElement = new TableSlateElement.TableCellSlateElement();
                            tableCellSlateElement.setIsHeader(isHeader);
                            if (NumberUtil.isNumber(colSpan)) {
                                tableCellSlateElement.setColSpan(Integer.parseInt(colSpan));

                            }
                            if (NumberUtil.isNumber(rowspan)) {
                                tableCellSlateElement.setRowSpan(Integer.parseInt(rowspan));
                            }
                            SlateNode childrenTd = HtmlParser.parseElemHtml(td);
                            tableCellSlateElement.getChildren().add(childrenTd);
                            tableRowSlateElement.getChildren().add(tableCellSlateElement);
                        }
                        tableSlateElement.getChildren().add(tableRowSlateElement);
                    }
                    tableSlateElement.setWidth("100%");
                    return tableSlateElement;
                }
            }
            ParagraphSlateElement<SlateNode> paragraphSlateElement = new ParagraphSlateElement<>();
            String html = HtmlUtil.removeHtmlTag(paragraphBlockData.getText(), "p");
            for (Node node : Jsoup.parse(html).body().childNodes()) {
                Element element = wrapperText(node);
                SlateNode parseElemHtml = HtmlParser.parseElemHtml(element);
                children.add(parseElemHtml);
            }
            paragraphSlateElement.setChildren(children);
            paragraphSlateElement.setId(docBlock.getId());
            return paragraphSlateElement;
        } else if (blockData instanceof TrackerItemBlockData) {
            TrackerItemBlockData trackerItemBlockData = (TrackerItemBlockData) blockData;
            TrackerItemSlateElement<SlateNode> trackerItemSlateElement = new TrackerItemSlateElement<>();
            List<SlateNode> children = new ArrayList<>();
            String refId = trackerItemBlockData.getRefId() == null ? null : trackerItemBlockData.getRefId().toString();
            trackerItemSlateElement.setRef(refId);
            for (Node node : Jsoup.parse(trackerItemBlockData.getName()).body().childNodes()) {
                TrackerItemSlateElement.TrackerItemTitleSlateElement trackerItemTitleSlateElement = new TrackerItemSlateElement.TrackerItemTitleSlateElement();
                children.add(trackerItemTitleSlateElement);
                List<SlateNode> subChildren = new ArrayList<>();
                Element element = wrapperText(node);
                SlateNode parseElemHtml = HtmlParser.parseElemHtml(element);
                subChildren.add(parseElemHtml);
                trackerItemTitleSlateElement.setChildren(subChildren);
                trackerItemTitleSlateElement.setRef(refId);
            }
            TrackerItemSlateElement.TrackerItemDescriptionSlateElement trackerItemDescriptionSlateElement = new TrackerItemSlateElement.TrackerItemDescriptionSlateElement();
            children.add(trackerItemDescriptionSlateElement);
            List<SlateNode> subChildren = new ArrayList<>();
            trackerItemDescriptionSlateElement.setChildren(subChildren);
            trackerItemDescriptionSlateElement.setRef(refId);

            String txt = StringUtils.isEmpty(trackerItemBlockData.getText()) ? "<p><span></span></p>" : trackerItemBlockData.getText();
            for (Node node : Jsoup.parse(txt).body().childNodes()) {
                Element element = wrapperText(node);
                SlateNode parseElemHtml = HtmlParser.parseElemHtml(element);
                subChildren.add(parseElemHtml);
            }
            trackerItemSlateElement.setChildren(children);


            TrackerItemSlateElement.TrackerItemExtraSlateElement trackerItemExtraSlateElement = new TrackerItemSlateElement.TrackerItemExtraSlateElement();
            children.add(trackerItemExtraSlateElement);
            List<SlateNode> subChildren2 = new ArrayList<>();
//            ParagraphSlateElement p = new ParagraphSlateElement<SlateNode>();
            SlateText text = new SlateText("");
//            p.getChildren().add(text);
            subChildren2.add(text);

            trackerItemExtraSlateElement.setChildren(subChildren2);
            trackerItemExtraSlateElement.setRef(refId);
            trackerItemSlateElement.setTrackerItem(fillTrackerItemVo(trackerItemBlockData.getTrackerItem()));
            trackerItemSlateElement.setId(docBlock.getId());
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
            List<SlateNode> children = new ArrayList<>();
            for (Node node : Jsoup.parse(headerBlockData.getText()).body().childNodes()) {
                Element element = wrapperText(node);
                SlateNode parseElemHtml = HtmlParser.parseElemHtml(element);
                children.add(parseElemHtml);
            }
            headerSlateElement.setChildren(children);
            headerSlateElement.setId(docBlock.getId());
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

    private static TrackerItemVo fillTrackerItemVo(TrackerItemBlockData.InnerTrackerItemVo trackerItemVo) {
        if (trackerItemVo == null) {
            trackerItemVo = new TrackerItemBlockData.InnerTrackerItemVo();
        }
        TrackerItemVo trackerItemVo2 = new TrackerItemVo();
        IdNameReference<ProjectVo> project = new IdNameReference<>();
        project.setId(trackerItemVo.getProjectId());
        trackerItemVo2.setProject(project);
        trackerItemVo2.setId(trackerItemVo.getId());
        IdNameReference<TrackerVo> trackerVo = new IdNameReference<>();
        trackerVo.setId(trackerItemVo.getTrackerId());
        trackerVo.setIcon(trackerItemVo.getTrackerIcon());
        trackerVo.setId(trackerItemVo.getTrackerId());
        trackerItemVo2.setTracker(trackerVo);

        trackerItemVo2.setRealEndDate(trackerItemVo.getRealEndDate());
        trackerItemVo2.setItemNo(trackerItemVo.getItemNo());
        trackerItemVo2.setAssignedDate(trackerItemVo.getAssignedDate());

        trackerItemVo2.setPriority(EnumItemVo.builder().id(trackerItemVo.getPriorityId()).build());
        trackerItemVo2.setProgress(trackerItemVo.getProgress());
        trackerItemVo2.setCloseDate(trackerItemVo.getCloseDate());
        trackerItemVo2.setEstimateWorkingHours(trackerItemVo.getEstimateWorkingHours());
        trackerItemVo2.setPlanEndDate(trackerItemVo.getPlanEndDate());
        trackerItemVo2.setPlanStartDate(trackerItemVo.getPlanStartDate());
        trackerItemVo2.setRegisteredWorkingHours(trackerItemVo.getRegisteredWorkingHours());
        trackerItemVo2.setRemainingWorkingHours(trackerItemVo.getRemainingWorkingHours());
        trackerItemVo2.setRevision(trackerItemVo.getRevision());
        trackerItemVo2.setStatusId(trackerItemVo.getStatusId());
        trackerItemVo2.setValues(trackerItemVo.getValues());
        return trackerItemVo2;
    }
}
