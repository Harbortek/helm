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

package com.harbortek.helm.tracker.entity.smartdoc.element.po.element.list;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElements;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.SlateElement;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@JsonTypeName(SlateElements.LIST)
public class ListSlateElement<T extends SlateNode> extends SlateElement {

    public ListSlateElement() {
        super();
        type = SlateElements.LIST;
    }

    private Boolean ordered; // 有序/无序
    private Long level = 0L; // 层级：0 1 2 ...

    @Override
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
//        String tagName = ordered ? "ol" : "ul";
//        sb.append("<" + tagName + ">");
        for (Object obj : getChildren()) {
            SlateNode child = (SlateNode) obj;
            if (StringUtils.isEmpty(child.toHtml())) {
                continue;
            }
            sb.append("<li>" + child.toHtml() + "</li>");
        }
//        sb.append("</" + tagName + ">");
        return sb.toString();
    }
}
