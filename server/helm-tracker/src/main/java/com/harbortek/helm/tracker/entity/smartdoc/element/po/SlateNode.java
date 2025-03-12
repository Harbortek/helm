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

import cn.hutool.core.lang.id.NanoId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.style.SlateStyle;
import com.harbortek.helm.util.IDUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@Data
public abstract class SlateNode implements Serializable {
    protected String type;

    private String id;
    protected List<SlateStyle> styles = new ArrayList();

    abstract public String toHtml();

    public String html() {
        String html = toHtml();
        for (SlateStyle style : styles) {
            html = style.styleToHtml(this, html);
        }
        return html;
    }

    public void setId(String id) {
        if (StringUtils.isEmpty(id)) {
            this.id = IDUtils.getShortId();
        } else {
            this.id = id;
        }
    }

    public String getId() {
        if (StringUtils.isEmpty(id)) {
            this.id = IDUtils.getShortId();
        }
        return id;
    }
}
