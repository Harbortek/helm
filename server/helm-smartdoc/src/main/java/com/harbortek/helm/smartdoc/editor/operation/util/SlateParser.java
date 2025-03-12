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
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.SlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElements;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.*;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.header.*;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.list.ListSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.paragraph.ParagraphSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.table.TableSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.title.TitleSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.trackerItem.TrackerItemSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.style.*;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.text.SlateText;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SlateParser {
    private static final ObjectMapper objectMapper = JsonUtils.getObjectMapper();

    public static List<SlateNode> parseArray(String jsonString) throws IOException {
        JSONArray jsonArray = new JSONArray(jsonString);
        return parseArray(jsonArray);
    }

    public static List<SlateNode> parseArray(JSONArray jsonArray) throws IOException {
        List<SlateNode> slateNodes = new ArrayList<>();
        for (Object item : jsonArray) {
            if (item instanceof JSONNull) {
                continue;
            }
            JSONObject obj = (JSONObject) item;
            SlateNode child = parseOne(obj);
            slateNodes.add(child);
        }
        return slateNodes;
    }

    public static SlateNode parseOne(String jsonString) throws IOException {
        return parseOne(new JSONObject(jsonString));
    }

    public static SlateNode parseOne(JSONObject obj) throws IOException {
        String type = obj.getStr("type");
        List<SlateStyle> styles = parseStyles(obj);
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
        } else if (SlateElements.LIST.equals(type)) {
            node = new ListSlateElement();
        } else {
            node = new SlateText();
        }
        BeanUtil.fillBeanWithMap(obj, node, CopyOptions.create().ignoreError().setIgnoreProperties(
                "styles"
        ));
        if (SlateElements.TRACKER_ITEM.equals(type)) {
            TrackerItemSlateElement item = (TrackerItemSlateElement)node;
            item.setTrackerItem(JsonUtils.toObject(obj.getStr("trackerItem"), TrackerItemVo.class));
        }
        node.setStyles(styles);
        String children = obj.getStr("children");
        if (node instanceof SlateElement<?>) {
            if (StringUtils.isNotEmpty(children)) {
                List<SlateNode> parseedChildren = parseArray(children);
                ((SlateElement) node).setChildren(parseedChildren);
            }
        }
        return node;
    }

    private static List<SlateStyle> parseStyles(JSONObject item) throws IOException {

        String textAlign = item.getStr("textAlign");

        List<SlateStyle> styles = new ArrayList<>();
        //justify包装
        if (StringUtils.isNotEmpty(textAlign)) {
            JustifyStyle justifyStyle = JustifyStyle.builder().textAlign(textAlign).build();
            styles.add(justifyStyle);
        }
        //indent包装
        String indent = item.getStr("indent");
        if (StringUtils.isNotEmpty(indent)) {
            IndentStyle indentStyle = IndentStyle.builder().indent(indent).build();
            styles.add(indentStyle);
        }
        //lineHeight包装
        String lineHeight = item.getStr("lineHeight");
        if (StringUtils.isNotEmpty(lineHeight)) {
            LineHeightStyle lineHeightElement = LineHeightStyle.builder().lineHeight(lineHeight).build();
            styles.add(lineHeightElement);
        }
        //color包装
        String color = item.getStr("color");
        String bgColor = item.getStr("bgColor");
        if (StringUtils.isNotEmpty(color) || StringUtils.isNotEmpty(bgColor)) {
            ColorStyle colorStyle = ColorStyle.builder().color(color).bgColor(bgColor).build();
            styles.add(colorStyle);
        }
        //style包装
        Boolean bold = item.getBool("bold");
        Boolean code = item.getBool("code");
        Boolean italic = item.getBool("italic");
        Boolean through = item.getBool("through");
        Boolean underline = item.getBool("underline");
        Boolean sup = item.getBool("sup");
        Boolean sub = item.getBool("sub");
        if (Boolean.TRUE.equals(bold) || Boolean.TRUE.equals(code) ||
                Boolean.TRUE.equals(italic) || Boolean.TRUE.equals(through)
                || Boolean.TRUE.equals(underline) || Boolean.TRUE.equals(sup) ||
                Boolean.TRUE.equals(sub)) {
            StyleedStyle styleedStyle = StyleedStyle.builder()
                    .bold(bold)
                    .code(code)
                    .italic(italic)
                    .through(through)
                    .underline(underline)
                    .sup(sup)
                    .sub(sub).build();
            styles.add(styleedStyle);
        }
        //fontSizeAndFamily包装
        String fontSize = item.getStr("fontSize");
        String fontFamily = item.getStr("fontFamily");
        if (StringUtils.isNotEmpty(fontSize) || StringUtils.isNotEmpty(fontFamily)) {
            FontSizeAndFamilyStyle fontSizeAndFamilyStyle = FontSizeAndFamilyStyle.builder()
                    .fontSize(fontSize)
                    .fontFamily(fontFamily).build();
            styles.add(fontSizeAndFamilyStyle);
        }
        return styles;
    }
}