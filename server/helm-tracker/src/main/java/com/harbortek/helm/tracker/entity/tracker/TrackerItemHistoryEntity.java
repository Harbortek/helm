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

package com.harbortek.helm.tracker.entity.tracker;

import com.harbortek.helm.common.annotation.EntityReference;
import com.harbortek.helm.common.entity.HistoryBaseEntity;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.system.entity.EnumItemEntity;
import com.harbortek.helm.system.entity.UserEntity;
import com.harbortek.helm.tracker.entity.plan.SprintEntity;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.util.*;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@NoArgsConstructor
@Table(value = "tracker_items_history")
public class TrackerItemHistoryEntity extends HistoryBaseEntity {

    @EntityReference(ProjectEntity.class)
    Long projectId;
    @EntityReference(TrackerEntity.class)
    Long trackerId;

//    @EntityReference(TrackerItemEntity.class)
    Long parentId;

    String itemNo;

    Long statusId;

    Long meaningId;
    @EntityReference(EnumItemEntity.class)
    Long priorityId;

    @EntityReference(UserEntity.class)
    Long ownerId;

    @EntityReference(UserEntity.class)
    Long assignedToId;

    Date assignedDate;

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

    /**
     * 进度
     */
    Integer progress;

    /**
     * 关闭时间
     */
    Date closeDate;

    /**
     * 预计花费工时
     */
    Integer estimateWorkingHours;
    /**
     * 已登记工时
     */
    Integer registeredWorkingHours;

    /**
     * 剩余工时
     */
    Integer remainingWorkingHours;

    /**
     * 迭代ID
     */
    @EntityReference(SprintEntity.class)
    Long sprintId;

    Map<Long,Object> values = new HashMap<>();

//    List<ItemRelation> relatedWorkItems = new ArrayList<>();
    List<Long> relatedWikis=new ArrayList<>();

    //	@EntityReference(AttachmentEntity.class)
    List<Long> attachments = new ArrayList<>();

    List<BaseIdentity> watchers = new ArrayList<>();

}
