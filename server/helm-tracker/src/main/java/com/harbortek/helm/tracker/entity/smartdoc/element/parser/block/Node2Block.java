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
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.list.ListSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.paragraph.ParagraphSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.title.TitleSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.trackerItem.TrackerItemSlateElement;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.List;

public class Node2Block {

    private static String unwrapper(String html) {
        List<Node> all = Jsoup.parse(html).body().childNodes();
        if (all.isEmpty()) {
            return "";
        }
        Node elem = all.get(0);
        StringBuilder sb = new StringBuilder();
        if (elem != null) {
            elem.childNodes().forEach(node -> {
                sb.append(node.outerHtml());
            });
        }
        return sb.toString();
    }

    public static DocBlock parse(SlateNode node) {
        DocBlock docBlock = null;
        if (node instanceof TitleSlateElement<?>) {
            TitleSlateElement titleSlateElement = (TitleSlateElement<?>) node;

            docBlock = DocBlock.builder()
                    .type(BlockTypes.TITLE)
                    .data(TitleBlockData.builder().text(
                            unwrapper(titleSlateElement.toHtml())
                    ).build())
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
                    .data(HeaderBlockData.builder().level(level).text(
                                    unwrapper(headerSlateElement.toHtml()))
                            .build())
                    .build();

        } else if (node instanceof TrackerItemSlateElement<?>) {
            TrackerItemSlateElement<?> trackerItemSlateElement = (TrackerItemSlateElement<?>) node;
            Long refId = null;
            if (trackerItemSlateElement.getRef() != null) {
                refId = Long.parseLong(trackerItemSlateElement.getRef());
            }
            TrackerItemVo trackerItemVo = trackerItemSlateElement.getTrackerItem();
            String type = BlockTypes.TRACKER_ITEM;
            if (trackerItemVo != null) {
                type = trackerItemVo.getTracker().getId().toString();
            }
            docBlock = DocBlock.builder()
                    .type(type)
                    .data(TrackerItemBlockData.builder().refId(refId).build())
                    .build();
        } else if (node instanceof ListSlateElement<?>) {
            ListSlateElement<?> listSlateElement = (ListSlateElement<?>) node;
            docBlock = DocBlock.builder()
                    .type(BlockTypes.PARAGRAPH)
                    .data(ParagraphBlockData.builder().text(
                            unwrapper((listSlateElement.toHtml()))).build())
                    .build();
        } else {
            docBlock = DocBlock.builder()
                    .type(BlockTypes.PARAGRAPH)
                    .data(ParagraphBlockData.builder().text(
                            unwrapper((node.toHtml()))).build())
                    .build();
        }
        docBlock.setId(node.getId());
        return docBlock;

    }
}
