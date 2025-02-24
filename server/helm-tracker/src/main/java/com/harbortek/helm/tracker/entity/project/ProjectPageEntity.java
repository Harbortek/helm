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

package com.harbortek.helm.tracker.entity.project;

import com.harbortek.helm.common.annotation.EntityReference;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=true)
@FieldNameConstants
@NoArgsConstructor
@Table(value = "project_pages")
public class ProjectPageEntity extends BaseEntity {

    Long projectId;

    Long parentId;

    @Builder.Default
    Boolean folder = false;

    Integer level;

    Integer order;

    String type;

    String componentType;

    String url;

    @EntityReference(TrackerEntity.class)
    Long trackerId;

    Long smartPageId;
    Long smartDocId;

    String definition;
    String blockTrackerItemRefs;
    String content;

    @Builder.Default
    Long revision=1L;

    List<BaseIdentity> watchers = new ArrayList<>();

    List<PageSettingTracker> pageSettingTrackers = new ArrayList<>();

//    @EntityReference(ProjectPageEntity.class)
//    Collection<Long> children = new ArrayList<>();
}
