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

package com.harbortek.helm.tracker.entity.review;

import com.harbortek.helm.common.annotation.EntityReference;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.system.entity.UserEntity;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.vo.review.ReviewStatus;
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
@EqualsAndHashCode(callSuper=true)
@FieldNameConstants
@NoArgsConstructor
@Table(value = "reviews")

public class ReviewEntity extends BaseEntity {

    @EntityReference(ProjectEntity.class)
    Long projectId;

    //ReviewStatusTypes
    String status;

    Double passRate;

    String dueDate;

    @EntityReference(UserEntity.class)
    Long creator;

    //完成评审用户人数
    List<Long> dones = new ArrayList<>();

    List<Long> reviewers = new ArrayList<>();


    List<Long> deliverables = new ArrayList<>();
    List<Long> documents = new ArrayList<>();
    List<Long> milestones = new ArrayList<>();
    List<Long> projectPlans = new ArrayList<>();
    List<Long> sprints = new ArrayList<>();
    List<Long> targetVersions = new ArrayList<>();
    List<Long> tasks = new ArrayList<>();
    List<Long> trackerItems = new ArrayList<>();

    List<ReviewStatus> reviewStatuses = new ArrayList<>();


}
