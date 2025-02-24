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

/*
 * 项目名称:浩博ELM协同平台
 * Copyright 南京浩博科技有限公司 2023 版权所有
 *
 */

package com.harbortek.helm.tracker.vo.tracker;

import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.layout.TrackerLayout;
import com.harbortek.helm.tracker.vo.tracker.nofitication.TrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransition;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.util.ObjectUtils;
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
@FieldNameConstants
@NoArgsConstructor
public class TrackerVo extends BaseVo {

    EnumItemVo trackerType;

    Long projectId;

    Integer ordinary;


    String defaultLayout;

    Boolean inheritFromTemplateInd = false;
    Boolean availableAsTemplateInd = false;
    Boolean workflowInd = false;
    Boolean visibleInd = false;

    List<PermissionGrantVo> trackerPermissions = new ArrayList<>();

    List<TrackerStatus> trackerStatuses = new ArrayList<>();

    List<TrackerStateTransition> trackerStateTransitions = new ArrayList<>();

    List<TrackerField> trackerFields = new ArrayList<>();

    List<TrackerLayout> trackerLayouts = new ArrayList<>();

    TrackerNotification trackerNotification = new TrackerNotification();

    public String getIcon(){
        if(ObjectUtils.isNotEmpty(super.getIcon())){
            return super.getIcon();
        }
        if (trackerType != null){
            return trackerType.getIcon();
        }
        return null;
    }

}
