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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.PoUtils;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElements;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.text.SlateText;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Data
@Builder
@JsonTypeName(SlateElements.FontSizeAndFamilySlateText)
public class FontSizeAndFamilyStyle extends SlateStyle {

    private String fontSize;
    private String fontFamily;

    public String styleToHtml(SlateNode node, String html) {

        if (!(node instanceof SlateText)) {
            return html;
        }
        if (StringUtils.isEmpty(fontSize) && StringUtils.isEmpty(fontFamily)) {
            return html;
        }
        Element elem = null;
        if (PoUtils.isPlainText(html)) {
            html = "<span>" + html + "</span>";
            Document doc = Jsoup.parse(html);
            elem = doc.body().child(0);
        } else {
            Document doc = Jsoup.parse(html);
            elem = doc.body().child(0); // html 是一个单一的元素
            String tagName = elem.tagName();
            if (!"span".equals(tagName)) {
                html = "<span>" + html + "</span>";
                doc = Jsoup.parse(html);
                elem = doc.body().child(0);
            }
            if (StringUtils.isNotEmpty(fontFamily)) {
                elem.attr("style", "font-family:" + fontFamily);
            }
            if (StringUtils.isNotEmpty(fontSize)) {
                elem.attr("style", "font-size:" + fontSize);
            }
            return elem.outerHtml();
        }
        return html;
    }

}
