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

package com.harbortek.helm.tracker.constants;

import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TextField;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.fields.WikiTextField;
import com.harbortek.helm.tracker.vo.tracker.layout.TrackerLayout;
import com.harbortek.helm.tracker.vo.tracker.nofitication.TrackerNotification;

import java.util.*;

/**
 * 内置Tracker
 */
public abstract class InternalTrackers {
    private static final TrackerField FIELD_NAME =
            TextField.builder().system(true).name("标题").systemProperty(SystemFields.NAME).id(-1L).build();
    private static final TrackerField FIELD_DESCRIPTION =
            WikiTextField.builder().system(true).name("描述").systemProperty(SystemFields.DESCRIPTION).id(-2L).build();
    private static final TrackerField
            FIELD_ITEM_NO =
            TextField.builder().system(true).name("工作项编号").systemProperty(SystemFields.ITEM_NO).id(-3L).build();

    private static final TrackerField
            FIELD_CREATE_BY =
            TextField.builder().system(true).name("创建者").systemProperty(SystemFields.CREATE_BY).id(-4L).build();

    private static final TrackerField
            FIELD_CREATE_DATE =
            TextField.builder().system(true).name("创建日期").systemProperty(SystemFields.CREATE_DATE).id(-5L).build();

    public static TrackerVo HEADING = TrackerVo.builder()
            .id(-1L)
            .name("章节")
            .trackerFields(
                    Arrays.asList(
                            FIELD_NAME,
                            FIELD_DESCRIPTION,
                            FIELD_ITEM_NO,
                            FIELD_CREATE_BY,
                            FIELD_CREATE_DATE
                    )
            )
            .trackerType(EnumItemVo.builder().name("章节").system(true).icon("chapter").color("").backgroundColor("").build())
            .trackerLayouts(Collections.<TrackerLayout>singletonList(
                    TrackerLayout.builder()
                                 .sections(Arrays.<String>asList(TrackerLayoutSections.DETAIL,
                                                                 TrackerLayoutSections.RELATED_ITEMS))
                                 .fields(Collections.<IdNameReference>singletonList(
                                         new IdNameReference(FIELD_NAME)
                                                                                   )).name("DETAIL").build())
            )
            .trackerNotification(new TrackerNotification())
            .build();

    public static TrackerVo TITLE = TrackerVo.builder()
            .id(-2L)
            .name("标题")
            .trackerFields(
                    Arrays.asList(
                            FIELD_NAME,
                            FIELD_DESCRIPTION,
                            FIELD_ITEM_NO,
                            FIELD_CREATE_BY,
                            FIELD_CREATE_DATE
                    )
            )
            .trackerType(EnumItemVo.builder().name("标题").system(true).icon("topic").color("").backgroundColor("").build())
            .trackerLayouts(Arrays.<TrackerLayout>asList(
                    TrackerLayout.builder()
                                 .sections(Arrays.<String>asList(TrackerLayoutSections.DETAIL,TrackerLayoutSections.RELATED_ITEMS))
                                 .fields(Collections.<IdNameReference>singletonList(
                                         new IdNameReference(FIELD_NAME)
                                                                                   )).name("DETAIL").build())
            )
            .trackerNotification(new TrackerNotification())
            .build();

    public static TrackerVo PARAGRAPH = TrackerVo.builder()
            .id(-3L)
            .name("段落")
            .trackerFields(
                    Arrays.asList(
                            FIELD_NAME,
                            FIELD_DESCRIPTION,
                            FIELD_ITEM_NO,
                            FIELD_CREATE_BY,
                            FIELD_CREATE_DATE
                    )
            )
            .trackerType(EnumItemVo.builder().name("段落").system(true).icon("paragraph").color("").backgroundColor("").build())
            .trackerLayouts(Collections.<TrackerLayout>singletonList(
                    TrackerLayout.builder()
                                 .sections(Arrays.<String>asList(TrackerLayoutSections.DETAIL,
                                                                 TrackerLayoutSections.RELATED_ITEMS))
                                 .fields(Collections.<IdNameReference>singletonList(
                                         new IdNameReference(FIELD_DESCRIPTION)
                                                                                   )
                                        ).name("DETAIL").build())
            )
            .trackerNotification(new TrackerNotification())
            .build();

    public static TrackerVo getTrackerVo(Long trackerId) {
        if (InternalTrackers.HEADING.getId().equals(trackerId)) {
            return InternalTrackers.HEADING;
        } else if (InternalTrackers.TITLE.getId().equals(trackerId)) {
            return InternalTrackers.TITLE;
        } else if (InternalTrackers.PARAGRAPH.getId().equals(trackerId)) {
            return InternalTrackers.PARAGRAPH;
        }
        return null;
    }

    public static void fillInternalTracker(TrackerItemVo trackerItemVo, TrackerItemEntity trackerItemEntity) {
        Long trackerId = trackerItemEntity.getTrackerId();
        IdNameReference<TrackerVo> trackerRef = new IdNameReference<>();
        if (Objects.equals(trackerId, InternalTrackers.TITLE.getId())) {
            trackerRef.setId(InternalTrackers.TITLE.getId());
            trackerRef.setName(InternalTrackers.TITLE.getName());
            trackerRef.setReferTo(InternalTrackers.TITLE);
            trackerItemVo.setTracker(trackerRef);
        } else if (Objects.equals(trackerId, InternalTrackers.HEADING.getId())) {
            trackerRef.setId(InternalTrackers.HEADING.getId());
            trackerRef.setName(InternalTrackers.HEADING.getName());
            trackerRef.setReferTo(InternalTrackers.HEADING);
            trackerItemVo.setTracker(trackerRef);

        } else if (Objects.equals(trackerId, InternalTrackers.PARAGRAPH.getId())) {
            trackerRef.setId(InternalTrackers.PARAGRAPH.getId());
            trackerRef.setName(InternalTrackers.PARAGRAPH.getName());
            trackerRef.setReferTo(InternalTrackers.PARAGRAPH);
            trackerItemVo.setTracker(trackerRef);
        }
    }


    public static List<TrackerItemVo> fillInternalTracker(List<TrackerItemVo> trackerItemVos, List<TrackerItemEntity> trackerItemEntities) {
        Map<Long, TrackerItemEntity> entityMap = new HashMap<>();
        for (TrackerItemEntity entity : trackerItemEntities) {
            entityMap.put(entity.getId(), entity);
        }
        trackerItemVos.forEach(t -> {
            TrackerItemEntity entity = entityMap.get(t.getId());
            Long trackerId = entity.getTrackerId();
            IdNameReference<TrackerVo> trackerRef = new IdNameReference<>();
            if (Objects.equals(trackerId, InternalTrackers.TITLE.getId())) {
                trackerRef.setId(InternalTrackers.TITLE.getId());
                trackerRef.setName(InternalTrackers.TITLE.getName());
                trackerRef.setReferTo(InternalTrackers.TITLE);
                t.setTracker(trackerRef);

            } else if (Objects.equals(trackerId, InternalTrackers.HEADING.getId())) {
                trackerRef.setId(InternalTrackers.HEADING.getId());
                trackerRef.setName(InternalTrackers.HEADING.getName());
                trackerRef.setReferTo(InternalTrackers.HEADING);
                t.setTracker(trackerRef);

            } else if (Objects.equals(trackerId, InternalTrackers.PARAGRAPH.getId())) {
                trackerRef.setId(InternalTrackers.PARAGRAPH.getId());
                trackerRef.setName(InternalTrackers.PARAGRAPH.getName());
                trackerRef.setReferTo(InternalTrackers.PARAGRAPH);
                t.setTracker(trackerRef);

            }

        });
        return trackerItemVos;
    }
}
