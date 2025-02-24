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

package com.harbortek.helm.smartdoc.editor.operation.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateText;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.*;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.header.*;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.paragraph.ParagraphSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.table.TableSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.title.TitleSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.trackerItem.TrackerItemSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.texts.EmptySlateText;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.texts.FontSizeAndFamilySlateText;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.texts.PureSlateText;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.texts.color.ColorSlateText;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.texts.style.StyleSlateText;
import com.harbortek.helm.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.dataflow.qual.Pure;
import org.docx4j.org.apache.xpath.operations.Bool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SlateParser {
    private static final ObjectMapper objectMapper = JsonUtils.getObjectMapper();

    public static List<SlateNode> parse(String jsonString) throws IOException {
        return objectMapper.readValue(jsonString,
                objectMapper.getTypeFactory().constructCollectionType(List.class, SlateNode.class));
    }

    public static SlateNode parseOne(String jsonString) throws IOException {
        return objectMapper.readValue(jsonString, SlateNode.class);
    }

    public static List<SlateNode> parse(JSONArray jsonArray) throws IOException {
        List<SlateNode> slateNodes = new ArrayList<>();
        for (Object item : jsonArray) {
            JSONObject obj = (JSONObject) item;

            JSONArray children = obj.getJSONArray("children");
            List<SlateNode> parseedChildren = null;
            if (children != null) {
                parseedChildren = parse(children);
                obj.set("children", null);
            }
            SlateNode node = parseOne(obj);
            if (parseedChildren != null) {
                ((SlateElement) node).setChildren(parseedChildren);
            }

            slateNodes.add(node);
        }
        return slateNodes;
    }

    public static SlateNode parseOne(JSONObject obj) throws IOException {
        String type = obj.getStr("type");
        SlateNode node = null;
        if (SlateElements.ATTACHMENT.equals(type)) {
            node = new AttachmentSlateElement();
        } else if (SlateElements.BLOCKQUOTE.equals(type)) {
            node = new BlockQuoteSlateElement<>();
        } else if (SlateElements.TABLE_ROW.equals(type)) {
            node = new TableSlateElement.TableRowSlateElement<>();
        } else if (SlateElements.TABLE_CELL.equals(type)) {
            node = new TableSlateElement.TableCellSlateElement<>();
        } else if (SlateElements.TABLE.equals(type)) {
            node = new TableSlateElement();
        } else if (SlateElements.CODE.equals(type)) {
            node = new CodeSlateElement<>();
        } else if (SlateElements.DIVIDER.equals(type)) {
            node = new DividerSlateElement<>();
        } else if (SlateElements.HEADER1.equals(type)) {
            node = new Header1SlateElement<>();
        } else if (SlateElements.HEADER2.equals(type)) {
            node = new Header2SlateElement<>();
        } else if (SlateElements.HEADER3.equals(type)) {
            node = new Header3SlateElement<>();
        } else if (SlateElements.HEADER4.equals(type)) {
            node = new Header4SlateElement<>();
        } else if (SlateElements.HEADER5.equals(type)) {
            node = new Header5SlateElement<>();
        } else if (SlateElements.IMAGE.equals(type)) {
            node = new ImageSlateElement<>();
        } else if (SlateElements.INDENT.equals(type)) {
            node = new IndentSlateElement<>();
        } else if (SlateElements.JUSTIFY.equals(type)) {
            node = new JustifySlateElement<>();
        } else if (SlateElements.LINE_HEIGHT.equals(type)) {
            node = new LineHeightSlateElement<>();
        } else if (SlateElements.PARAGRAPH.equals(type)) {
            node = new ParagraphSlateElement<>();
        } else if (SlateElements.PRE.equals(type)) {
            node = new PreSlateElement<>();
        } else if (SlateElements.TODO.equals(type)) {
            node = new TodoSlateElement<>();
        } else if (SlateElements.TITLE.equals(type)) {
            node = new TitleSlateElement<>();
        } else if (SlateElements.TRACKER_ITEM.equals(type)) {
            node = new TrackerItemSlateElement<>();
        } else if (SlateElements.TRACKER_ITEM_TITLE.equals(type)) {
            node = new TrackerItemSlateElement.TrackerItemTitleSlateElement();
        } else if (SlateElements.TRACKER_ITEM_DESCRIPTION.equals(type)) {
            node = new TrackerItemSlateElement.TrackerItemDescriptionSlateElement();
        } else if (SlateElements.TRACKER_ITEM_EXTRA.equals(type)) {
            node = new TrackerItemSlateElement.TrackerItemExtraSlateElement();
        } else if (SlateElements.LINK.equals(type)) {
            node = new LinkSlateElement();
        } else {
            String color = obj.getStr("color");
            String fontSize = obj.getStr("fontSize");
            String fontFamily = obj.getStr("fontFamily");
            Object bold = obj.get("bold");
            String text = obj.getStr("text");
            if (text == null) {
                node = new PureSlateText();
            } else if ("".equals(text)) {
                node = new EmptySlateText();
            } else if (StringUtils.isNotEmpty(color)) {
                node = new ColorSlateText();
            } else if (StringUtils.isNotEmpty(fontSize) || StringUtils.isNotEmpty(fontFamily)) {
                node = new FontSizeAndFamilySlateText();
            } else if (bold != null) {
                node = new StyleSlateText();
            } else {
                node = new SlateText();
            }
        }
        BeanUtil.fillBeanWithMap(obj, node, true);
        return node;
    }
}