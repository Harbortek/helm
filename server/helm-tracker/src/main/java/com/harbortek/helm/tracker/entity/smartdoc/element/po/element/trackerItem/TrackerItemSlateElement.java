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

package com.harbortek.helm.tracker.entity.smartdoc.element.po.element.trackerItem;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.SlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateElements;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import lombok.Data;
import org.springframework.data.annotation.Transient;

@Data
@JsonTypeName(SlateElements.TRACKER_ITEM)
public class TrackerItemSlateElement<SlateText> extends SlateElement {

    public TrackerItemSlateElement() {
        super();
        type = SlateElements.TRACKER_ITEM;
    }

    private String ref;
    private String refHistoryId;
    @Transient
    private TrackerItemVo trackerItem;

    @Override
    public String toHtml() {
        return StrUtil.format("""
                <p data-x-ref={}>{}</p>
                """, ref, getChildrenHtml());
    }

    @Data
    @JsonTypeName(SlateElements.TRACKER_ITEM_TITLE)
    public static class TrackerItemTitleSlateElement extends SlateElement {
        private String ref;
        private String refHistoryId;
        public TrackerItemTitleSlateElement() {
            super();
            type = SlateElements.TRACKER_ITEM_TITLE;
        }
        @Override
        public String toHtml() {
            return StrUtil.format("""
                    <p data-x-ref={}>{}</p>
                    """, ref, getChildrenHtml());
        }
    }

    @Data
    @JsonTypeName(SlateElements.TRACKER_ITEM_DESCRIPTION)
    public static class TrackerItemDescriptionSlateElement extends SlateElement {
        private String ref;
        private String refHistoryId;
        public TrackerItemDescriptionSlateElement() {
            super();
            type = SlateElements.TRACKER_ITEM_DESCRIPTION;
        }

        @Override
        public String toHtml() {
            return StrUtil.format("""
                    <p data-x-ref={}>{}</p>
                    """, ref, getChildrenHtml());
        }
    }

    @Data
    @JsonTypeName(SlateElements.TRACKER_ITEM_EXTRA)
    public static class TrackerItemExtraSlateElement extends SlateElement {
        private String ref;
        private String refHistoryId;
        public TrackerItemExtraSlateElement() {
            super();
            type = SlateElements.TRACKER_ITEM_EXTRA;
        }

        @Override
        public String toHtml() {
            return StrUtil.format("""
                    <p data-x-ref={}>{}</p>
                    """, ref, getChildrenHtml());
        }
    }
}

