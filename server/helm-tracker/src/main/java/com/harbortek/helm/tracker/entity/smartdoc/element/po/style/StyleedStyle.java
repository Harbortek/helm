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

package com.harbortek.helm.tracker.entity.smartdoc.element.po.style;

import com.harbortek.helm.tracker.entity.smartdoc.element.po.PoUtils;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateDescendant;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.text.StyledText;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Data
public class StyleedSlateStyle extends SlateStyle {

    private String textAlign;

    public String styleToHtml(SlateDescendant node, String html) {
        if (!(node instanceof StyledText)) {
            return html;
        }
        StyledText textNode = (StyledText) node;
        if (PoUtils.isPlainText(html)) {
            return genStyledHtml(textNode, html);
        }
        // 解析 HTML
        Document doc = Jsoup.parse(html);
        Element elem = doc.body().child(0); // html 是一个单一的元素
        String tagName = elem.tagName();
        if ("br".equals(tagName)) {
            return genStyledHtml(textNode, "<br>");
        }
        String nhtml = genStyledHtml(textNode, elem.html());
        elem.html(nhtml);
        return elem.outerHtml();
    }

    private String genStyledHtml(StyledText textNode, String html) {
        String styledHtml = html;
        if (textNode.getBold()) styledHtml = "<strong>" + styledHtml + "</strong>";
        if (textNode.getCode()) styledHtml = "<code>" + styledHtml + "</code>";
        if (textNode.getItalic()) styledHtml = "<em>" + styledHtml + "</em>";
        if (textNode.getUnderline()) styledHtml = "<u>" + styledHtml + "</u>";
        if (textNode.getThrough()) styledHtml = "<s>" + styledHtml + "</s>";
        if (textNode.getSub()) styledHtml = "<sub>" + styledHtml + "</sub>";
        if (textNode.getSup()) styledHtml = "<sup>" + styledHtml + "</sup>";
        return styledHtml;
    }
}
