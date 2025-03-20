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
import cn.hutool.http.HtmlUtil;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.constants.BlockTypes;
import com.harbortek.helm.tracker.entity.block.*;

import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.header.*;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.list.ListSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.paragraph.ParagraphSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.table.TableSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.title.TitleSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.trackerItem.TrackerItemSlateElement;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.List;
import java.util.Optional;

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
                            unwrapper(titleSlateElement.html())
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
                                    unwrapper(headerSlateElement.html()))
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
            List<SlateNode> children = trackerItemSlateElement.getChildren();
            String name = HtmlUtil.cleanHtmlTag(children.get(0).html());
            String description = children.get(1).html();
            Long trackerId = trackerItemVo.getTracker() != null ? trackerItemVo.getTracker().getId() : null;
            docBlock = DocBlock.builder()
                    .type(type)
                    .data(TrackerItemBlockData.builder().refId(refId).name(name)
                            .text(description).trackerId(trackerId)
                            .trackerItem(fillTrackerItemVo(trackerItemVo)).build())
                    .build();
        } else if (node instanceof ListSlateElement<?>) {
            ListSlateElement<?> listSlateElement = (ListSlateElement<?>) node;
            docBlock = DocBlock.builder()
                    .type(BlockTypes.LIST_ITEM)
                    .data(ListItemBlockData.builder()
                            .ordered(listSlateElement.getOrdered())
                            .level(listSlateElement.getLevel()).text(
                                    unwrapper((listSlateElement.html()))).build())
                    .build();
        } else if (node instanceof TableSlateElement<?>) {
            TableSlateElement<?> tableSlateElement = (TableSlateElement<?>) node;
            docBlock = DocBlock.builder()
                    .type(BlockTypes.PARAGRAPH)
                    .data(ParagraphBlockData.builder().text(
                            (tableSlateElement.html())).build())
                    .build();
        } else {
            docBlock = DocBlock.builder()
                    .type(BlockTypes.PARAGRAPH)
                    .data(ParagraphBlockData.builder().text(
                            unwrapper((node.html()))).build())
                    .build();
        }
        docBlock.setId(node.getId());
        return docBlock;

    }

    private static TrackerItemBlockData.InnerTrackerItemVo fillTrackerItemVo(TrackerItemVo trackerItemVo) {
        TrackerVo tracker = trackerItemVo.getTracker() != null ?
                TrackerVo.builder()
                        .id(trackerItemVo.getTracker().getId())
                        .name(trackerItemVo.getTracker().getName())
                        .icon(trackerItemVo.getTracker().getIcon())
                        .build()
                : new TrackerVo();
        if (trackerItemVo.getTracker() == null) {
            trackerItemVo.setTracker(new IdNameReference<>());
        }
        if (trackerItemVo.getProject() == null) {
            trackerItemVo.setProject(new IdNameReference<>());
        }
        ProjectVo projectVo = trackerItemVo.getProject() != null ?
                ProjectVo.builder().id(trackerItemVo.getProject().getId()).build()
                : new ProjectVo();
        EnumItemVo trackerType = tracker.getTrackerType();
        if (trackerType == null) {
            trackerType = new EnumItemVo();
        }
        TrackerItemBlockData.InnerTrackerItemVo trackerItemVo2 = new TrackerItemBlockData.InnerTrackerItemVo();
        trackerItemVo2.setProjectId(trackerItemVo.getProject().getId());
        trackerItemVo2.setId(trackerItemVo.getId());
        trackerItemVo2.setTrackerIcon(tracker.getIcon());
        trackerItemVo2.setTrackerColor(trackerType.getColor());
        trackerItemVo2.setTrackerBackgroundColor(trackerType.getBackgroundColor());
        trackerItemVo2.setRealEndDate(trackerItemVo.getRealEndDate());
        trackerItemVo2.setItemNo(trackerItemVo.getItemNo());
        trackerItemVo2.setProjectKeyName(projectVo.getKeyName());
        trackerItemVo2.setAssignedDate(trackerItemVo.getAssignedDate());
        trackerItemVo2.setTrackerId(trackerItemVo.getTracker().getId());
//        Optional.ofNullable(trackerItemVo.getSprint())
//                .ifPresent(value -> trackerItemVo2.setSprintId(value.getId()));
        Optional.ofNullable(trackerItemVo.getOwner())
                .ifPresent(value -> trackerItemVo2.setOwnerId(value.getId()));
        Optional.ofNullable(trackerItemVo.getMeaning())
                .ifPresent(value -> trackerItemVo2.setMeaningId(value.getId()));
        Optional.ofNullable(trackerItemVo.getPriority())
                .ifPresent(value -> trackerItemVo2.setPriorityId(value.getId()));
        Optional.ofNullable(trackerItemVo.getAssignedTo())
                .ifPresent(value -> trackerItemVo2.setAssignedTo(value.getId()));
        Optional.ofNullable(trackerItemVo.getSeverity())
                .ifPresent(value -> trackerItemVo2.setSeverityId(value.getId()));

        trackerItemVo2.setPriorityId(trackerItemVo.getPriority().getId());
        trackerItemVo2.setProgress(trackerItemVo.getProgress());
        trackerItemVo2.setCloseDate(trackerItemVo.getCloseDate());
        trackerItemVo2.setEstimateWorkingHours(trackerItemVo.getEstimateWorkingHours());
        trackerItemVo2.setPlanEndDate(trackerItemVo.getPlanEndDate());
        trackerItemVo2.setPlanStartDate(trackerItemVo.getPlanStartDate());
        trackerItemVo2.setRegisteredWorkingHours(trackerItemVo.getRegisteredWorkingHours());
        trackerItemVo2.setRemainingWorkingHours(trackerItemVo.getRemainingWorkingHours());
        trackerItemVo2.setRevision(trackerItemVo.getRevision());
        trackerItemVo2.setStatusId(trackerItemVo.getStatusId());
        trackerItemVo2.setValues(trackerItemVo.getValues());
        return trackerItemVo2;
    }
}
