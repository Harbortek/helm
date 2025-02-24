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

package com.harbortek.helm.tracker.service;

import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.log.AttachmentVo;
import com.harbortek.helm.tracker.vo.plan.DeliverableVo;
import com.harbortek.helm.tracker.vo.plan.GanttVo;
import com.harbortek.helm.tracker.vo.plan.PlanVo;
import com.harbortek.helm.tracker.vo.plan.SprintVo;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public interface PlanService {

    GanttVo buildGantt(Long projectId);
    Collection<PlanVo> findPlans(Long projectId);

    Collection<PlanVo> findMilestones(Long projectId);

    PlanVo findOnePlan(Long id);

    PlanVo createPlan(PlanVo planVo);

    PlanVo updatePlan(PlanVo planVo);

    void deletePlan(Long id);

    void updateDeliverables(Long planId, List<DeliverableVo> deliverables);

    AttachmentVo uploadAttachment(Long deliverableId, AttachmentVo attachmentVo);

    void updateDeliverable(Long deliverableId, DeliverableVo deliverableVo);

    void autoPlans(Long projectId);

    Collection<DeliverableVo> findDeliverables(Long projectId);

    Collection<PlanVo> findWaitExecutePlans(Long projectId, Long currentUserId);

    void associateSprints(Long planId, List<SprintVo> sprints);

    void associateTrackerItems(Long planId, List<TrackerItemVo> trackerItems);

    void dissociateSprints(Long planId, List<SprintVo> sprints);

    void dissociateTrackerItems(Long planId, List<TrackerItemVo> trackerItems);

    void changeOrder(List<PlanVo> planVos);

    void importMPP(Long projectId, InputStream mppFile);
}
