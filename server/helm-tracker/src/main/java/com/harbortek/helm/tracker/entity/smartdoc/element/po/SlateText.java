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

package com.harbortek.helm.tracker.entity.smartdoc.element.po;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.elements.SlateElements;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonTypeName(SlateElements.TEXT)
public class SlateText implements SlateNode, SlateDescendant {
    private String type = SlateElements.TEXT;
    private String text="";

    private String fontSize;
    private String fontFamily;
    private Boolean bold =false;
    private Boolean code = false;
    private Boolean italic = false;
    private Boolean through = false;
    private Boolean underline = false;
    private Boolean sup = false;
    private Boolean sub = false;
    private String color;
    private String bgColor;
    public SlateText(String text) {
        this.text = text;
    }

    public SlateText() {
    }
}
