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

package com.harbortek.helm.tracker.entity.smartdoc.element.po.text;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElements;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.style.SlateStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonTypeName(SlateElements.TEXT)
public class SlateText extends SlateNode {
    private String type = SlateElements.TEXT;
    private String text = "";

    public SlateText(String text) {
        this.text = text;
    }

    public SlateText() {
    }

    protected List<SlateStyle> styles = new ArrayList<>();

    public List<SlateStyle> getStyles() {
        return styles;
    }

    public void setStyles(List<SlateStyle> styles) {
        this.styles = styles;
    }
    @Override
    public String toHtml() {
        return this.text;
    }
}
