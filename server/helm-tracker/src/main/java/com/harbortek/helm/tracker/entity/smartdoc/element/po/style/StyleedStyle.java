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
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.text.SlateText;
import lombok.Builder;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Data
@Builder
public class StyleedStyle extends SlateStyle {

    private Boolean bold;
    private Boolean code;
    private Boolean italic;
    private Boolean underline;
    private Boolean through;
    private Boolean sub;
    private Boolean sup;

    public String styleToHtml(SlateNode textNode, String html) {
        if (!(textNode instanceof SlateText)) {
            return html;
        }
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

    private String genStyledHtml(SlateNode textNode, String html) {
        String styledHtml = html;
        if (bold) styledHtml = "<strong>" + styledHtml + "</strong>";
        if (code) styledHtml = "<code>" + styledHtml + "</code>";
        if (italic) styledHtml = "<em>" + styledHtml + "</em>";
        if (underline) styledHtml = "<u>" + styledHtml + "</u>";
        if (through) styledHtml = "<s>" + styledHtml + "</s>";
        if (sub) styledHtml = "<sub>" + styledHtml + "</sub>";
        if (sup) styledHtml = "<sup>" + styledHtml + "</sup>";
        return styledHtml;
    }
}
