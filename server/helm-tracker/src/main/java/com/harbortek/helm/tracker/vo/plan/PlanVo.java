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

package com.harbortek.helm.tracker.vo.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldNameConstants
public class PlanVo extends BaseVo {
    String type;

    Long parentId;

    @Builder.Default
    Integer ordinary = 0;

    String seqNumber;

    String itemNo;

    /**
     * 项目ID
     */
    Long projectId;

    /**
     * 负责人
     */
//    @EntityInclude({"id", "name", "email", "icon"})
    private IdNameReference<UserVo> owner;

//    private Long ownerId;

    /**
     * 计划开始时间
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8") // 返回到前台
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime planStartDate;

    /**
     * 计划结束时间
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8") // 返回到前台
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime planEndDate;

    /**
     * 实际开始时间
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8") // 返回到前台
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime realStartDate;
    /**
     * 实际结束时间
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8") // 返回到前台
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime realEndDate;


    /**
     * 进度
     */
    Integer progress=0;

    @Builder.Default
    Integer duration =1;

//    ProjectStatusVo status;
//    Long statusId;
    @Builder.Default
    Boolean finished = false;

    @Builder.Default
    List<PlanDependencyVo> preTasks = new ArrayList<>();

    @Builder.Default
    List<PlanDependencyVo> postTasks = new ArrayList<>();

    /**
     * 交付物ID
     */

    List<DeliverableVo> deliverables = new ArrayList<>();

    /**
     * 关联的迭代ID
     */
    List<SprintVo> sprints = new ArrayList<>();

    /**
     * 关联的工作项ID
     */
    List<TrackerItemVo> items = new ArrayList<>();
}
