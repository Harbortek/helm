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

package com.harbortek.helm.tracker.entity.plan;

import com.harbortek.helm.common.annotation.EntityReference;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.system.entity.EnumItemEntity;
import com.harbortek.helm.system.vo.EnumItemVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=true)
@FieldNameConstants
@NoArgsConstructor
@Table(value = "target_versions")
public class TargetVersionEntity extends BaseEntity {
    Long projectId;

    @EntityReference(EnumItemEntity.class)
    Long statusId;
    /**
     * 计划开始时间
     */
    Date planStartDate;

    /**
     * 计划结束时间
     */
    Date planEndDate;

    /**
     * 实际开始时间
     */
    Date realStartDate;
    /**
     * 实际结束时间
     */
    Date realEndDate;

    Integer progress;

    Double totalWorkingHours;
    Double completedWorkingHours;
    Double remainingWorkingHours;
}
