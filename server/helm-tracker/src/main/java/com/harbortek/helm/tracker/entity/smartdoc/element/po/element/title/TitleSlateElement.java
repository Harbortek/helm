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

package com.harbortek.helm.tracker.entity.smartdoc.element.po.element.title;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.SlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElements;
import lombok.Data;

@Data
@JsonTypeName(SlateElements.TITLE)
public class TitleSlateElement<SlateText> extends SlateElement {
    public TitleSlateElement() {
        super();
        type = SlateElements.TITLE;
    }

    @Override
    public String toHtml() {
        return StrUtil.format("""
                <p data-w-e-type="title">{}</p>
                """, getChildrenHtml());
    }
}
