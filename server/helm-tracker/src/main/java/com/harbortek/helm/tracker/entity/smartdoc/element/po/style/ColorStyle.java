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

import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateDescendant;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.text.ColorText;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Data
public class ColorSlateStyle extends SlateStyle {

    private String textAlign;

    public String styleToHtml(SlateDescendant textNode, String html) {
        if (textNode instanceof ColorText) {
            ColorText colorText = (ColorText) textNode;
            if (StringUtils.isEmpty(colorText.getColor())
                    && StringUtils.isEmpty(colorText.getBgColor())) {
                return html;
            }

            // 解析 HTML
            Document doc = Jsoup.parse("<span>" + html + "</span>");
            Element elem = doc.body().child(0); // html 是一个单一的元素

            // 设置 text-indent 样式
            if (StringUtils.isNotEmpty(colorText.getColor())) {
                elem.attr("style", "color: " + colorText.getColor());
            }
            if (StringUtils.isNotEmpty(colorText.getBgColor())) {
                elem.attr("style", "background-color: " + colorText.getBgColor());
            }
            // 返回修改后的 HTML
            return elem.outerHtml();
        }
        return html;
    }
}
