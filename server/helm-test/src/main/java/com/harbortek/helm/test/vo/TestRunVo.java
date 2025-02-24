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

package com.harbortek.helm.test.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.harbortek.helm.common.annotation.EntityInclude;
import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.vo.plan.SprintVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldNameConstants
public class TestRunVo extends BaseVo {

    /**
     * 项目ID
     */
    Long projectId;

    EnumItemVo status;

    String meaning;

    /**
     * 测试阶段
     */
    EnumItemVo phase;

    /**
     * 负责人
     */
    @EntityInclude({"id", "name", "email", "icon"})
    private UserVo owner;

    /**
     * 计划开始时间
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8") // 返回到前台
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date planStartDate;

    /**
     * 计划结束时间
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8") // 返回到前台
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date planEndDate;


    /**
     * 进度
     */
    Integer progress;

    Integer passRate;

    SprintVo sprint;

    Integer totalCount;

    Integer passedCount;

    Integer executedCount;
}
