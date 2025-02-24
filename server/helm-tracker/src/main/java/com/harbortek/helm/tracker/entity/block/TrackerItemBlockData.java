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

package com.harbortek.helm.tracker.entity.block;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.common.entity.IdName;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.constants.BlockTypes;
import com.harbortek.helm.tracker.entity.log.HyperlinkEntity;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkVo;
import com.harbortek.helm.tracker.vo.log.AttachmentVo;
import com.harbortek.helm.tracker.vo.plan.SprintVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.util.JsonUtils;
import com.harbortek.helm.util.ObjectUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;

import java.util.*;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldNameConstants
@JsonTypeName(value = BlockTypes.TRACKER_ITEM)
public class TrackerItemBlockData extends DocBlockData {

    private Long trackerId;
    private String name;
    private Boolean isTrackerItemLink = false;
    @Transient
    //赋值的时候可选(作为VO使用)
    private InnerTrackerItemVo trackerItem;

    @Data
    public static class InnerTrackerItemVo extends BaseVo {
        Long trackerId;
        String projectKeyName;

        String trackerIcon;
        String trackerBackgroundColor;
        String trackerColor;

        String itemNo;

        Long projectId;
        Long ownerId;

        Long priorityId;

        Long severityId;

        Long revision;

        Long statusId;

        Long meaningId;

        Date planStartDate;

        Date planEndDate;

        Date realStartDate;
        Date realEndDate;

        Integer progress;

        Date closeDate;

        Double estimateWorkingHours;
        Double registeredWorkingHours;

        Double remainingWorkingHours;

        Long sprintId;

        Map<Long, String> values = new HashMap<>();

        Long assignedTo;

        Date assignedDate;

    }
}
