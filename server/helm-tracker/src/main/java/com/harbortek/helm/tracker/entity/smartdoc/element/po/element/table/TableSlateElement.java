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

package com.harbortek.helm.tracker.entity.smartdoc.element.po.element.table;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.SlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElements;
import lombok.Data;

@Data
@JsonTypeName(SlateElements.TABLE)
public class TableSlateElement<TableRowSlateElement> extends SlateElement {
    private String type = SlateElements.TABLE;
    private String width = "auto";

    @Override
    public String toHtml() {
        return StrUtil.format("""
                <table style="width: {};"><tbody>{}</tbody></table>
                """, width, getChildrenHtml());
    }

    @Data
    @JsonTypeName(SlateElements.TABLE_CELL)
    public static class TableCellSlateElement<SlateText> extends SlateElement {
        private String type = SlateElements.TABLE_CELL;
        private Boolean isHeader = false;// td/th 只作用于第一行
        private Integer colSpan = 1;
        private Integer rowSpan = 1;
        private String width = "auto";// 只作用于第一行（尚未考虑单元格合并！）

        @Override
        public String toHtml() {
            String tag = isHeader ? "th" : "td";
            return StrUtil.format("""
                    <{} colSpan="{}" rowSpan="{}" width="{}">{}</{}>
                    """, tag, colSpan, rowSpan, width, getChildrenHtml(), tag);
        }
    }

    @Data
    @JsonTypeName(SlateElements.TABLE_ROW)
    public static class TableRowSlateElement<TableCellSlateElement> extends SlateElement {
        private String type = SlateElements.TABLE_ROW;

        @Override
        public String toHtml() {
            return StrUtil.format("""
                    <tr>{}</tr>
                    """, getChildrenHtml());
        }
    }
}

