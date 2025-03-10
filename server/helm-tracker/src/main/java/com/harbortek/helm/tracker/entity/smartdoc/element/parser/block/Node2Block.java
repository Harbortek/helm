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

package com.harbortek.helm.tracker.entity.smartdoc.element.parser.block;

import cn.hutool.core.lang.id.NanoId;
import com.harbortek.helm.tracker.constants.BlockTypes;
import com.harbortek.helm.tracker.entity.block.*;

import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.header.*;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.paragraph.ParagraphSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.title.TitleSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.trackerItem.TrackerItemSlateElement;

public class Node2Block {

    public static DocBlock parse(SlateNode node) {
        DocBlock docBlock = null;
        if (node instanceof TitleSlateElement<?>) {
            TitleSlateElement titleSlateElement = (TitleSlateElement<?>) node;

            docBlock = DocBlock.builder()
                    .type(BlockTypes.TITLE)
                    .data(TitleBlockData.builder().text(titleSlateElement.toHtml()).build())
                    .build();
        } else if (node instanceof HeaderSlateElement<?>) {
            HeaderSlateElement<?> headerSlateElement = (HeaderSlateElement<?>) node;
            Long level = 1L;
            if (headerSlateElement instanceof Header1SlateElement<?>) {
                level = 1L;
            } else if (headerSlateElement instanceof Header2SlateElement<?>) {
                level = 2L;
            } else if (headerSlateElement instanceof Header3SlateElement<?>) {
                level = 3L;
            } else if (headerSlateElement instanceof Header4SlateElement<?>) {
                level = 4L;
            } else if (headerSlateElement instanceof Header5SlateElement<?>) {
                level = 5L;
            }
            docBlock = DocBlock.builder()
                    .type(BlockTypes.HEADING)
                    .data(HeaderBlockData.builder().level(level).text(headerSlateElement.toHtml()).build())
                    .build();
        } else if (node instanceof ParagraphSlateElement<?>) {
            ParagraphSlateElement<?> paragraphSlateElement = (ParagraphSlateElement<?>) node;
            docBlock = DocBlock.builder()
                    .type(BlockTypes.PARAGRAPH)
                    .data(ParagraphBlockData.builder().text(paragraphSlateElement.toHtml()).build())
                    .build();
        } else if (node instanceof TrackerItemSlateElement<?>) {
            TrackerItemSlateElement<?> trackerItemSlateElement = (TrackerItemSlateElement<?>) node;
            Long refId = Long.parseLong(trackerItemSlateElement.getRef());
            docBlock = DocBlock.builder()
                    .type(BlockTypes.TRACKER_ITEM)
                    .data(TrackerItemBlockData.builder().refId(refId).build())
                    .build();
        }
        docBlock.setId(NanoId.randomNanoId());
        return docBlock;
    }

}
