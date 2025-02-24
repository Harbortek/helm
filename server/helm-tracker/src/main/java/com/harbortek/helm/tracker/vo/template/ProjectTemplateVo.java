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

package com.harbortek.helm.tracker.vo.template;


import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.vo.ProjectRoleMemberVo;
import com.harbortek.helm.tracker.vo.block.DocVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkVo;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.tracker.vo.smartpage.SmartPageVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldNameConstants
public class ProjectTemplateVo extends BaseVo {
    String trackerPrefixToReplace;

    String location;

    Long maxItemNo = 0L;

    @Transient
    List<ProjectRoleMemberVo> roles = new ArrayList<>();
    @Transient
    List<PermissionGrantVo> permissionGrants = new ArrayList<>();
    @Transient
    List<TrackerVo> trackers = new ArrayList<>();

    @Transient
    List<TrackerItemVo> trackerItems = new ArrayList<>();

    @Transient
    List<TrackerLinkVo> trackerLinks = new ArrayList<>();

    @Transient
    List<ProjectPageVo> pages = new ArrayList<>();

    @Transient
    List<DocVo> docs = new ArrayList<>();

    @Transient
    List<EnumItemVo> enumItems = new ArrayList<>();

    @Transient
    List<SmartPageVo> smartPages = new ArrayList<>();

}
