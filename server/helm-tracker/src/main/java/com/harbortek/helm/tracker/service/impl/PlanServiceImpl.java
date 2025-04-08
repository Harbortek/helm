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

package com.harbortek.helm.tracker.service.impl;

import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.service.UserService;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.constants.*;
import com.harbortek.helm.tracker.dao.*;
import com.harbortek.helm.tracker.entity.link.TrackerLinkEntity;
import com.harbortek.helm.tracker.entity.link.TrackerLinkTypeEntity;
import com.harbortek.helm.tracker.entity.plan.PlanEntity;
import com.harbortek.helm.tracker.entity.plan.SprintEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.service.PlanService;
import com.harbortek.helm.tracker.service.SprintService;
import com.harbortek.helm.tracker.vo.plan.GanttVo;
import com.harbortek.helm.tracker.vo.plan.LinkVo;
import com.harbortek.helm.tracker.vo.plan.PlanDependencyVo;
import com.harbortek.helm.tracker.vo.plan.PlanVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.DateUtils;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.tree.TreeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
public class PlanServiceImpl implements PlanService {
    @Autowired
    PlanDao planDao;

    @Autowired
    DeliverableDao deliverableDao;

    @Autowired
    ProjectDao projectDao;

    @Autowired
    AttachmentDao attachmentDao;

    @Autowired
    TargetVersionDao targetVersionDao;

    @Autowired
    SprintDao sprintDao;

    @Autowired
    TrackerItemDao trackerItemDao;

    @Autowired
    TrackerLinkDao trackerLinkDao;

    @Autowired
    TrackerLinkTypeDao trackerLinkTypeDao;

    @Autowired
    UserService userService;

    @Autowired
    SprintService sprintService;

    @Autowired
    EnumService enumService;

    @Override
    public GanttVo buildGantt(Long projectId, Long versionId) {
        GanttVo ganttVo = new GanttVo();

        List<PlanEntity> planEntities = new ArrayList<>();
        List<SprintEntity> sprintEntities = sprintDao.findSprintByTargetVersion(projectId, versionId);

        EnumItemVo closed = enumService.findOneEnumItemByCode(projectId, EnumCodes.TRACKER_STATUS_MEANING, TrackerStatusMeaning.CLOSED);

        sprintEntities.forEach(sprint -> {
            PlanEntity plan = PlanEntity.builder().id(sprint.getId()).name(sprint.getName()).projectId(sprint.getProjectId())
                    .itemNo(sprint.getItemNo()).planStartDate(sprint.getPlanStartDate()).planEndDate(sprint.getPlanEndDate())
                    .realStartDate(sprint.getRealStartDate()).realEndDate(sprint.getRealEndDate())
                    .type(PlanTypes.TYPE_GROUP).ownerId(sprint.getOwnerId())
                    .duration(DateUtils.daysBetween(sprint.getPlanStartDate(), sprint.getPlanEndDate()))
                    .progress(sprint.getProgress()).build();
            planEntities.add(plan);

            List<TrackerItemEntity> trackerItems = trackerItemDao.findBySprintIds(projectId, List.of(sprint.getId()));
            trackerItems.forEach(item -> {
                PlanEntity task = PlanEntity.builder().parentId(plan.getId()).id(item.getId()).name(item.getName()).trackerId(item.getTrackerId()).projectId(item.getProjectId())
                        .statusId(item.getStatusId()).meaningId(item.getMeaningId()).priorityId(item.getPriorityId()).severityId(item.getSeverityId())
                        .itemNo(item.getItemNo()).planStartDate(item.getPlanStartDate()).planEndDate(item.getPlanEndDate())
                        .realStartDate(sprint.getRealStartDate()).realEndDate(item.getRealEndDate())
                        .type(PlanTypes.TYPE_TASK).ownerId(item.getOwnerId())
                        .progress(item.getProgress()).duration(DateUtils.daysBetween(item.getPlanStartDate(), item.getPlanEndDate()))
                        .finished(Objects.equals(item.getMeaningId(), closed.getId())).build();
                planEntities.add(task);
            });
        });
        List<PlanVo> planVos = DataUtils.toVo(planEntities, PlanVo.class);

        Map<Long, List<PlanDependencyVo>> sourceMap = new LinkedHashMap<>();
        Map<Long, List<PlanDependencyVo>> targetMap = new LinkedHashMap<>();

        TrackerLinkTypeEntity linkType = trackerLinkTypeDao.findByCode(Associations.DEPENDS_ON.getId(), projectId);

        List<TrackerLinkEntity> dependencyEntities = trackerLinkDao.findByItemIds(ObjectUtils.ids(planEntities))
                .stream().filter(link -> Objects.equals(linkType.getId(), link.getLinkTypeId())).toList();
        dependencyEntities.forEach(link -> {
            List<PlanDependencyVo> sourceLinks = sourceMap.computeIfAbsent(link.getSourceItemId(), k -> new ArrayList<>());
            PlanVo target = ObjectUtils.findById(planVos, link.getTargetItemId(), PlanVo.class);
            if (target != null) {
                sourceLinks.add(PlanDependencyVo.builder().id(link.getTargetItemId()).linkType(PlanDependencyTypes.FINISH_START)
                        .seqNumber(target.getSeqNumber()).build());
            }

            List<PlanDependencyVo> targetLinks = targetMap.computeIfAbsent(link.getTargetItemId(), k -> new ArrayList<>());
            PlanVo source = ObjectUtils.findById(planVos, link.getSourceItemId(), PlanVo.class);
            if (source != null) {
                targetLinks.add(PlanDependencyVo.builder().id(link.getSourceItemId()).linkType(PlanDependencyTypes.FINISH_START)
                        .seqNumber(source.getSeqNumber()).build());
            }
        });
        planVos.forEach(vo -> {
            vo.setPreTasks(targetMap.getOrDefault(vo.getId(), new ArrayList<>()));
            vo.setPostTasks(sourceMap.getOrDefault(vo.getId(), new ArrayList<>()));
        });

        ganttVo.setTasks(TreeUtils.treeToList(TreeUtils.listToTree(planVos, "id", "parentId")));

        List<LinkVo> links = new ArrayList<>();
        dependencyEntities.forEach(link -> {
            links.add(LinkVo.builder().id(link.getId()).source(link.getSourceItemId()).target(link.getTargetItemId()).type(
                    PlanDependencyTypes.map(PlanDependencyTypes.FINISH_START)).build());
        });
        ganttVo.setLinks(links);

        return ganttVo;
    }

    @Override
    public PlanVo updatePlan(PlanVo planVo) {
        //只允许工作项进行修改。迭代不能修改

        TrackerItemEntity trackerItemEntity = trackerItemDao.findById(planVo.getId(), TrackerItemEntity.class);
        if (trackerItemEntity != null) {
            trackerItemEntity.setPlanStartDate(planVo.getPlanStartDate());
            trackerItemEntity.setPlanEndDate(planVo.getPlanEndDate());
            int duration = DateUtils.daysBetween(planVo.getPlanStartDate(), planVo.getPlanEndDate());
            if (duration > 0) {
                trackerItemEntity.setEstimateWorkingHours(duration * 8.0);
            }
            trackerItemDao.update(trackerItemEntity);
        }

        if (ObjectUtils.isNotEmpty(planVo.getParentId())) {
            sprintService.syncSprintWorkingHours(planVo.getProjectId(), planVo.getParentId());
        }
        return planVo;
    }
}
