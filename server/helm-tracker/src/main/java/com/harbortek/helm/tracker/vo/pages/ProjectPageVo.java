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

package com.harbortek.helm.tracker.vo.pages;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.tracker.vo.smartpage.SystemVariableVo;
import com.harbortek.helm.tracker.entity.project.PageSettingTracker;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldNameConstants
public class ProjectPageVo extends BaseVo {
    Long parentId;

    Long projectId;

    Boolean folder = false;

    String type;

    Integer level;
    Integer order;

    String componentType;

    String url;

    /**
     * 默认工作项
     */
    IdNameReference<TrackerVo> tracker;

    @Builder.Default
    Long revision=1L;

    String definition;

    Long smartPageId;
    List<SystemVariableVo> systemVariables;

    Long smartDocId;

    String blockTrackerItemRefs;
    List<ProjectPageVo> children = new ArrayList<>();

    List<BaseIdentity> watchers = new ArrayList<>();

    /**
     * 可选工作项
     */
    List<PageSettingTracker> pageSettingTrackers = new ArrayList<>();
}
