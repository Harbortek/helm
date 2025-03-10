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

import com.harbortek.helm.tracker.entity.smartdoc.element.po.style.SlateStyle;

import java.util.ArrayList;
import java.util.List;

public abstract class SlateElement<T extends SlateNode> extends SlateNode {
    protected String type;
    private List<T> children = new ArrayList<>();


    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    protected List<SlateStyle> styles = new ArrayList<>();

    public List<SlateStyle> getStyles() {
        return styles;
    }

    public void setStyles(List<SlateStyle> styles) {
        this.styles = styles;
    }

    public String getChildrenHtml() {
        StringBuilder sb = new StringBuilder();
        for (SlateNode child : children) {
            sb.append(child.toHtml());
        }
        return sb.toString();
    }


}
