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
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElements;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.text.SlateText;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Builder
@Data
@JsonTypeName(SlateElements.COLOR_SLATE_TEXT)
public class ColorStyle extends SlateStyle {

    private String color;
    private String bgColor;

    public String styleToHtml(SlateNode textNode, String html) {
        if (!(textNode instanceof SlateText)) return html;
        if (StringUtils.isEmpty(color)
                && StringUtils.isEmpty(bgColor)) {
            return html;
        }

        // 解析 HTML
        Document doc = Jsoup.parse("<span>" + html + "</span>");
        Element elem = doc.body().child(0); // html 是一个单一的元素

        // 设置 text-indent 样式
        if (StringUtils.isNotEmpty(color)) {
            elem.attr("style", "color: " + color);
        }
        if (StringUtils.isNotEmpty(bgColor)) {
            elem.attr("style", "background-color: " + bgColor);
        }
        // 返回修改后的 HTML
        return elem.outerHtml();
    }
}
