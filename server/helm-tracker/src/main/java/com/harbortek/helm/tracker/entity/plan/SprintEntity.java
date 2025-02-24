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
import com.harbortek.helm.system.entity.UserEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=true)
@FieldNameConstants
@NoArgsConstructor
@Table(value = "sprints")
public class SprintEntity extends BaseEntity implements Planable{

    /**
     * 项目ID
     */
    Long projectId;

    /**
     * 编号
     */
    String itemNo;


    /**
     * 负责人
     */
    @EntityReference(UserEntity.class)
    Long ownerId;

    /**
     * 计划开始时间
     */
    LocalDateTime planStartDate;

    /**
     * 计划结束时间
     */
    LocalDateTime planEndDate;

    /**
     * 实际开始时间
     */
    LocalDateTime realStartDate;
    /**
     * 实际结束时间
     */
    LocalDateTime realEndDate;


    /**
     * 进度
     */
    Integer progress;


    Integer duration;

    @EntityReference(EnumItemEntity.class)
    private Long statusId;

    private String meaning;

    @EntityReference(TargetVersionEntity.class)
    private Long targetVersionId;

    /**
     * 关联的工作项ID
     */
    @Builder.Default
    List<Long> items = new ArrayList<>();
}
