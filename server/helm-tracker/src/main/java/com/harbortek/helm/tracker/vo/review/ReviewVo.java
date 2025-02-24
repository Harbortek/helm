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

package com.harbortek.helm.tracker.vo.review;

import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.vo.ProjectVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ReviewVo extends BaseVo {

    IdNameReference<ProjectVo> project;

    //ReviewStatusTypes
    String status;

    IdNameReference<UserVo> creator;

    String dueDate;

    List<Long> dones = new ArrayList<>();

    Integer total;

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

    public int getTotal() {
        return deliverables.size() + documents.size() + milestones.size() +
                projectPlans.size() + sprints.size() + targetVersions.size() +
                tasks.size() + trackerItems.size();
    }


    //通过的状态
    Integer passedItems = 0;

    //未通过的状态
    Integer unPassedItems = 0;

    //建议通过的状态
    Integer suggestPassedItems = 0;
    //未评审的状态
    Integer unReviewedItems = 0;

}