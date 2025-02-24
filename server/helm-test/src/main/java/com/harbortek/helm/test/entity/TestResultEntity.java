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

package com.harbortek.helm.test.entity;

import com.harbortek.helm.common.annotation.EntityReference;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.system.entity.EnumItemEntity;
import com.harbortek.helm.system.entity.UserEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.vo.tracker.test.TestStepResult;
import com.harbortek.helm.tracker.vo.tracker.test.TrackerTestStep;
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
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@NoArgsConstructor
@Table(value = "test_results")
public class TestResultEntity extends BaseEntity {
    /**
     * 测试运行ID
     */
    @EntityReference(TestRunEntity.class)
    Long testRunId;

    /**
     * 测试用例ID
     */
    @EntityReference(TrackerItemEntity.class)
    Long testCaseId;

    String itemNo;

    /**
     * 执行者ID
     */
    @EntityReference(UserEntity.class)
    Long executorId;

    /**
     * 测试结果ID
     */
    @EntityReference(EnumItemEntity.class)
    Long resultId;

    /**
     * 测试结果描述
     */
    String resultDesc;

//    /**
//     * 测试步骤
//     */
//    List<TrackerTestStep> testSteps =new ArrayList<>();

    /**
     * 测试步骤结果
     */
    List<TestStepResult> testStepResults =new ArrayList<>();
}
