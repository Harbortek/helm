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

import cn.hutool.core.collection.CollectionUtil;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.system.service.FileService;
import com.harbortek.helm.system.service.UserService;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.constants.PlanDependencyTypes;
import com.harbortek.helm.tracker.constants.PlanTypes;
import com.harbortek.helm.tracker.dao.AttachmentDao;
import com.harbortek.helm.tracker.dao.DeliverableDao;
import com.harbortek.helm.tracker.dao.PlanDao;
import com.harbortek.helm.tracker.dao.ProjectDao;
import com.harbortek.helm.tracker.entity.log.AttachmentEntity;
import com.harbortek.helm.tracker.entity.plan.DeliverableEntity;
import com.harbortek.helm.tracker.entity.plan.PlanDependencyEntity;
import com.harbortek.helm.tracker.entity.plan.PlanEntity;
import com.harbortek.helm.tracker.service.PlanService;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.log.AttachmentVo;
import com.harbortek.helm.tracker.vo.plan.*;
import com.harbortek.helm.util.*;
import com.harbortek.helm.util.tree.TreeNode;
import com.harbortek.helm.util.tree.TreeNodeAction;
import com.harbortek.helm.util.tree.TreeUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.mpxj.*;
import net.sf.mpxj.reader.UniversalProjectReader;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


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
    FileService fileService;

    @Autowired
    UserService userService;

    @Override
    public GanttVo buildGantt(Long projectId) {
        GanttVo ganttVo = new GanttVo();
        List<PlanEntity> planEntities = planDao.findPlans(projectId);
        List<PlanVo> planVos = DataUtils.toVo(planEntities, PlanVo.class);

        List<PlanDependencyEntity> dependencyEntities = planDao.findPlanDependencies(projectId);
        Map<Long, List<PlanDependencyVo>> sourceMap = new LinkedHashMap<>();
        Map<Long, List<PlanDependencyVo>> targetMap = new LinkedHashMap<>();
        dependencyEntities.forEach(link -> {
            List<PlanDependencyVo> sourceLinks = sourceMap.computeIfAbsent(link.getSourceId(), k -> new ArrayList<>());
            PlanVo target = ObjectUtils.findById(planVos, link.getTargetId(), PlanVo.class);
            if (target != null) {
                sourceLinks.add(PlanDependencyVo.builder().id(link.getTargetId()).linkType(link.getType())
                                                .seqNumber(target.getSeqNumber()).build());
            }

            List<PlanDependencyVo> targetLinks = targetMap.computeIfAbsent(link.getTargetId(), k -> new ArrayList<>());
            PlanVo source = ObjectUtils.findById(planVos, link.getSourceId(), PlanVo.class);
            if (source != null) {
                targetLinks.add(PlanDependencyVo.builder().id(link.getSourceId()).linkType(link.getType())
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
            links.add(LinkVo.builder().id(link.getId()).source(link.getSourceId()).target(link.getTargetId()).type(
                    PlanDependencyTypes.map(link.getType())).build());
        });
        ganttVo.setLinks(links);

        return ganttVo;
    }

    @Override
    public Collection<PlanVo> findPlans(Long projectId) {
        List<PlanEntity> planEntities = planDao.findPlans(projectId);
        List<PlanVo> planVos = DataUtils.toVo(planEntities, PlanVo.class);

        List<PlanDependencyEntity> dependencyEntities = planDao.findPlanDependencies(projectId);
        Map<Long, List<PlanDependencyVo>> sourceMap = new LinkedHashMap<>();
        Map<Long, List<PlanDependencyVo>> targetMap = new LinkedHashMap<>();
        dependencyEntities.forEach(link -> {
            List<PlanDependencyVo> sourceLinks = sourceMap.computeIfAbsent(link.getSourceId(), k -> new ArrayList<>());
            PlanVo target = ObjectUtils.findById(planVos, link.getTargetId(), PlanVo.class);
            sourceLinks.add(PlanDependencyVo.builder().id(link.getTargetId()).linkType(link.getType())
                                            .seqNumber(target.getSeqNumber()).build());

            List<PlanDependencyVo> targetLinks = targetMap.computeIfAbsent(link.getTargetId(), k -> new ArrayList<>());
            PlanVo source = ObjectUtils.findById(planVos, link.getSourceId(), PlanVo.class);
            targetLinks.add(PlanDependencyVo.builder().id(link.getSourceId()).linkType(link.getType())
                                            .seqNumber(source.getSeqNumber()).build());
        });
        planVos.forEach(vo -> {
            vo.setPreTasks(targetMap.getOrDefault(vo.getId(), new ArrayList<>()));
            vo.setPostTasks(sourceMap.getOrDefault(vo.getId(), new ArrayList<>()));
        });

        return TreeUtils.treeToList(TreeUtils.listToTree(planVos, "id", "parentId"));
    }

    @Override
    public Collection<PlanVo> findMilestones(Long projectId) {
        List<PlanEntity> planEntities = planDao.findMilestones(projectId);
        return DataUtils.toVo(planEntities, PlanVo.class);
    }

    @Override
    public PlanVo findOnePlan(Long planId) {
        PlanEntity plan = planDao.findById(planId);
        Long projectId = plan.getProjectId();
        List<PlanDependencyEntity> dependencyEntities = planDao.findPlanDependencies(projectId, planId);
        Map<Long, List<PlanDependencyVo>> sourceMap = new LinkedHashMap<>();
        Map<Long, List<PlanDependencyVo>> targetMap = new LinkedHashMap<>();
        dependencyEntities.forEach(link -> {
            List<PlanDependencyVo> sourceLinks = sourceMap.computeIfAbsent(link.getSourceId(), k -> new ArrayList<>());
            sourceLinks.add(PlanDependencyVo.builder().id(link.getTargetId()).linkType(link.getType()).build());

            List<PlanDependencyVo> targetLinks = targetMap.computeIfAbsent(link.getTargetId(), k -> new ArrayList<>());
            targetLinks.add(PlanDependencyVo.builder().id(link.getSourceId()).linkType(link.getType()).build());
        });
        PlanVo planVo = DataUtils.toVo(plan, PlanVo.class);
        List<PlanDependencyVo> preTasks = targetMap.getOrDefault(planVo.getId(), new ArrayList<>());
        List<PlanDependencyVo> postTasks = sourceMap.getOrDefault(planVo.getId(), new ArrayList<>());
        DataUtils.reload(preTasks, PlanEntity.class, new String[]{BaseEntity.Fields.name});
        DataUtils.reload(postTasks, PlanEntity.class, new String[]{BaseEntity.Fields.name});
        planVo.setPreTasks(preTasks);
        planVo.setPostTasks(postTasks);
        return planVo;
    }

    @Override
    public PlanVo createPlan(PlanVo planVo) {
        planVo.setId(IDUtils.getId());
        PlanEntity entity = DataUtils.toEntity(planVo, PlanEntity.class);
        entity.setItemNo(projectDao.getNextItemNo(planVo.getProjectId()));
        entity = planDao.createPlan(entity);

        final Long planId = entity.getId();

        updateDependency(planVo);

        calcParentWithSave(entity);

        refreshSeqNumber(planVo.getProjectId());

        return DataUtils.toVo(entity, PlanVo.class);
    }

    private void updateDependency(PlanVo planVo) {
        Long projectId = planVo.getProjectId();
        Long planId = planVo.getId();

        List<PlanDependencyEntity> linkAsTarget = new ArrayList<>();
        List<PlanDependencyVo> preTasks = planVo.getPreTasks();
        preTasks.forEach(dep -> {
            PlanDependencyEntity link =
                    PlanDependencyEntity.builder().id(IDUtils.getId()).projectId(projectId).sourceId(dep.getId())
                                        .targetId(planId)
                                        .type(dep.getLinkType()).build();
            linkAsTarget.add(link);
        });


        List<PlanDependencyEntity> linkAsSource = new ArrayList<>();
        List<PlanDependencyVo> postTasks = planVo.getPostTasks();
        postTasks.forEach(dep -> {
            PlanDependencyEntity link =
                    PlanDependencyEntity.builder().id(IDUtils.getId()).projectId(projectId).sourceId(planId)
                                        .targetId(dep.getId())
                                        .type(dep.getLinkType()).build();
            linkAsSource.add(link);
        });

        planDao.updateDependencies(projectId, planId, CollectionUtils.union(linkAsSource, linkAsTarget));
    }

    private void refreshSeqNumber(Long projectId) {
        List<PlanEntity> planEntities = planDao.findPlans(projectId);
        List<TreeNode<PlanEntity>> treeNodes = TreeUtils.listToTree(planEntities, "id", "parentId");
        TreeUtils.traversTree(null, treeNodes, new TreeNodeAction<PlanEntity>() {
            @Override
            public void doAction(TreeNode<PlanEntity> parent, int index, TreeNode<PlanEntity> node) {
                String seqNumber = "";
                if (parent != null) {
                    seqNumber += parent.getObject().getSeqNumber() + ".";
                }
                seqNumber += (index + 1);
                node.getObject().setSeqNumber(seqNumber);
            }
        });
        List<PlanEntity> data = TreeUtils.treeToList(treeNodes);
        AtomicInteger index = new AtomicInteger(0);
        data.forEach(item->item.setOrdinary(index.getAndIncrement()));
        data.forEach(item -> {System.out.println(item.getSeqNumber() + " " + item.getName());});
        planDao.batchUpdateSeqNumber(data);
    }

    private void calcParentWithSave(PlanEntity entity) {
        if (ObjectUtils.isNotEmpty(entity.getParentId())) {
            List<PlanEntity> children = planDao.findByParentId(entity.getParentId());

            PlanEntity parent = planDao.findById(entity.getParentId());
            parent.setPlanStartDate(calcPlanStartDateForParent(children));
            parent.setPlanEndDate(calcPlanEndDateForParent(children));
            parent.setProgress(calcProgressForParent(children));
            int progress = parent.getProgress() != null ? parent.getProgress() : 0;
            if (progress < 100 && progress > 0) {
                parent.setFinished(false);
            } else if (progress == 100) {
                parent.setFinished(true);
            }

            planDao.updatePlan(parent);
            calcParentWithSave(parent);
        }
    }

    private LocalDateTime calcPlanStartDateForParent(List<PlanEntity> children) {
        return children.stream()
                       .min((a, b) -> {return CompareUtils.compare(a.getPlanStartDate(), b.getPlanStartDate());}).get()
                       .getPlanStartDate();
    }

    private LocalDateTime calcPlanEndDateForParent(List<PlanEntity> children) {
        return children.stream().max((a, b) -> {return CompareUtils.compare(a.getPlanEndDate(), b.getPlanEndDate());})
                       .get()
                       .getPlanEndDate();
    }

    private Integer calcProgressForParent(List<PlanEntity> children) {

        double sum = 0.0;
        double totalDays = 0.0;
        for (PlanEntity child : children) {
            if (PlanTypes.TYPE_MILE_STONE.equalsIgnoreCase(child.getType())) {
                continue;
            }
            double progress = child.getProgress() != null ? child.getProgress() : 0;
            int duration = child.getDuration() != null ? child.getDuration() : 0;
            sum += progress / 100 * duration;
            totalDays += duration;
        }
        if (totalDays > 0) {
            return Long.valueOf(Math.round(sum / totalDays * 100)).intValue();
        }
        return null;
    }

    @Override
    public PlanVo updatePlan(PlanVo planVo) {
        PlanEntity entity = planDao.findById(planVo.getId());

        BeanCopyUtils.copyWithoutNullProperties(planVo, entity);

        if (StringUtils.isEmpty(entity.getItemNo())) {
            entity.setItemNo(projectDao.getNextItemNo(planVo.getProjectId()));
        }

        int progress = planVo.getProgress() != null ? planVo.getProgress() : 0;
        if (progress < 100 && progress > 0) {
            entity.setFinished(false);
        } else if (progress == 100) {
            entity.setFinished(true);
        }

        entity = planDao.updatePlan(entity);

        updateDependency(planVo);

        calcParentWithSave(entity);

        refreshSeqNumber(planVo.getProjectId());

        return DataUtils.toVo(entity, PlanVo.class);
    }

    @Override
    public void deletePlan(Long planId) {
        PlanEntity entity = planDao.findById(planId);
        Long projectId = entity.getProjectId();
        planDao.deletePlan(planId);
        if (PlanTypes.TYPE_MILE_STONE.equals(entity.getType())) {
            deliverableDao.deleteDeliverableByPlanId(projectId, planId);
        }
        planDao.deletePlanDependencies(projectId, planId);

        calcParentWithSave(entity);
    }

    @Override
    public void updateDeliverables(Long planId, List<DeliverableVo> deliverables) {
        PlanEntity planEntity = planDao.findById(planId);
        Long projectId = planEntity.getProjectId();
        List<DeliverableEntity> oldValues = deliverableDao.findDeliverables(projectId, planId);
        List<DeliverableEntity> newValues = new ArrayList<>();
        AtomicInteger index = new AtomicInteger(0);
        Long currentUserId = SecurityUtils.getCurrentUser().getId();
        deliverables.forEach(deliverableVo -> {
            DeliverableEntity existed =
                    oldValues.stream().filter(s -> {return s.getId().equals(deliverableVo.getId());}).findFirst()
                             .orElse(DeliverableEntity.builder().id(IDUtils.getId()).projectId(projectId)
                                                      .milestoneId(planId).createBy(currentUserId)
//                                                      .createDate(new Date())
                                                      .lastModifiedBy(currentUserId)
                                                      .lastModifiedDate(new Date()).build());
            existed.setName(deliverableVo.getName());
            existed.setType(deliverableVo.getType());
            existed.setOrdinary(index.getAndIncrement());
            newValues.add(existed);
        });

        List<DeliverableEntity> deleted = new ArrayList<>(CollectionUtils.subtract(oldValues, newValues));
        deleted.forEach(s -> s.setDeleted(true));
        newValues.addAll(deleted);

        if (!newValues.isEmpty()) {
//            deliverableDao.batchUpdateDeliverables(newValues);
            deliverableDao.batchCreateDeliverables(newValues);
        }
        Collection<Long> ids = ObjectUtils.ids(newValues);
        planEntity.setDeliverables(new ArrayList<>(ids));
        planDao.updatePlan(planEntity);
    }

    @Override
    public AttachmentVo uploadAttachment(Long deliverableId, AttachmentVo attachmentVo) {
        AttachmentEntity attachment =
                AttachmentEntity.builder().id(IDUtils.getId()).name(attachmentVo.getName()).objectId(deliverableId)
                                .filePath(attachmentVo.getFilePath()).fileSize(attachmentVo.getFileSize()).build();
        attachment.setCreateBy(SecurityUtils.getCurrentUser().getId());
        attachmentDao.createAttachment(attachment);

        DeliverableEntity item = deliverableDao.findById(deliverableId);
        item.setAttachmentId(attachment.getId());
        item.setCommitted(true);
        deliverableDao.updateDeliverable(item);

        return DataUtils.toVo(attachmentDao.fineById(attachment.getId()), AttachmentVo.class);
    }

    @Override
    public void updateDeliverable(Long deliverableId, DeliverableVo deliverableVo) {
        DeliverableEntity old = deliverableDao.findById(deliverableId);

        old.setUrl(deliverableVo.getUrl());
        old.setCommitted(deliverableVo.getCommitted());

        deliverableDao.updateDeliverable(old);
    }

    @Override
    public void autoPlans(Long projectId) {
        GanttVo ganttVo = buildGantt(projectId);
        Collection<PlanVo> tasks = ganttVo.getTasks();
        Map<Long, PlanVo> taskMap = CollectionUtil.fieldValueMap(tasks, "id");
        Collection<LinkVo> links = ganttVo.getLinks();

        //找出有后序但是没前序的任务
        Collection<PlanVo> withPostPlans =
                tasks.stream().filter(item -> {
                    return !item.getPostTasks().isEmpty() && item.getPreTasks().isEmpty() &&
                            !PlanTypes.TYPE_GROUP.equals(item.getType());
                }).collect(
                        Collectors.toList());
        autoPlanFromStart(ObjectUtils.ids(withPostPlans), taskMap);
        Collection<PlanEntity> planEntities = DataUtils.toEntity(taskMap.values(), PlanEntity.class);
        planEntities.forEach(plan -> {
            calcParent(plan, planEntities);
        });

        planDao.batchUpdateStartEndDate(planEntities);
    }

    @Override
    public Collection<DeliverableVo> findDeliverables(Long projectId) {
        List<DeliverableEntity> deliverableEntities = deliverableDao.findDeliverables(projectId);
        return DataUtils.toVo(deliverableEntities, DeliverableVo.class);
    }

    @Override
    public Collection<PlanVo> findWaitExecutePlans(Long projectId, Long currentUserId) {
        return DataUtils.toVo(planDao.findWaitExecutePlans(projectId, currentUserId), PlanVo.class);
    }

    @Override
    public void associateSprints(Long planId, List<SprintVo> sprints) {
        PlanEntity planEntity = planDao.findById(planId);
        planEntity.getSprints().addAll(ObjectUtils.ids(sprints));
        planDao.updatePlan(planEntity);
    }

    @Override
    public void associateTrackerItems(Long planId, List<TrackerItemVo> trackerItems) {
        PlanEntity planEntity = planDao.findById(planId);
        planEntity.getItems().addAll(ObjectUtils.ids(trackerItems));
        planDao.updatePlan(planEntity);
    }

    @Override
    public void dissociateSprints(Long planId, List<SprintVo> sprints) {
        PlanEntity planEntity = planDao.findById(planId);
        planEntity.getSprints().removeAll(ObjectUtils.ids(sprints));
        planDao.updatePlan(planEntity);
    }

    @Override
    public void dissociateTrackerItems(Long planId, List<TrackerItemVo> trackerItems) {
        PlanEntity planEntity = planDao.findById(planId);
        planEntity.getItems().removeAll(ObjectUtils.ids(trackerItems));
        planDao.updatePlan(planEntity);
    }

    @Override
    public void changeOrder(List<PlanVo> planVos) {
        Collection<PlanEntity> planEntities = DataUtils.toEntity(planVos, PlanEntity.class);
        AtomicInteger index = new AtomicInteger(1);
        planEntities.forEach(p -> p.setOrdinary(index.getAndIncrement()));
        planDao.batchChangeOrder(planEntities);
        if (!planEntities.isEmpty()) {
            refreshSeqNumber(planEntities.iterator().next().getProjectId());
        }
    }

    @Override
    public void importMPP(Long projectId,
                          InputStream mppFile) {
        try {
            UniversalProjectReader reader = new UniversalProjectReader();
            ProjectFile project = reader.read(mppFile);
            List<Task> tasks = project.getChildTasks().get(0).getChildTasks();
            List<PlanEntity> plans = buildPlansFromTasks(projectId, null, tasks);
            Map<Integer, PlanEntity> taskIdMap = new HashMap<>();
            plans.forEach(p -> {
                taskIdMap.put(p.getOrdinary(), p);
                p.setOrdinary(0);
            });

            List<PlanDependencyEntity> dependencies = buildDependenciesFromTasks(projectId, tasks, taskIdMap);

            planDao.deletePlansByProjectId(projectId);
            planDao.batchCreatePlans(plans);

            planDao.deletePlanDependenciesByProjectId(projectId);
            planDao.batchCreatePlanDependencies(dependencies);

            refreshSeqNumber(projectId);


        } catch (Exception e) {
            log.error("MPP导入失败", e);
            throw new ServiceException("MPP导入失败");
        }
    }

    private List<PlanDependencyEntity> buildDependenciesFromTasks(Long projectId, List<Task> tasks,
                                                                  Map<Integer, PlanEntity> taskIdMap) {
        List<PlanDependencyEntity> planDependencyEntities = new ArrayList<>();
        tasks.forEach(task -> {
            task.getPredecessors().forEach(predecessor -> {

                PlanDependencyEntity link =
                        PlanDependencyEntity.builder().id(IDUtils.getId()).projectId(projectId)
                                            .sourceId(taskIdMap.get(predecessor.getPredecessorTask().getID()).getId())
                                            .targetId(taskIdMap.get(task.getID()).getId())
                                            .type(getDependencyType(predecessor)).build();
                planDependencyEntities.add(link);
            });

            if (ObjectUtils.isNotEmpty(task.getChildTasks())) {
                planDependencyEntities.addAll(buildDependenciesFromTasks(projectId, task.getChildTasks(), taskIdMap));
            }

        });
        return planDependencyEntities;

    }

    private String getDependencyType(Relation relation) {
        if (RelationType.FINISH_START.equals(relation.getType())) {
            return PlanDependencyTypes.FINISH_START;
        } else if (RelationType.FINISH_FINISH.equals(relation.getType())) {
            return PlanDependencyTypes.FINISH_FINISH;
        } else if (RelationType.START_START.equals(relation.getType())) {
            return PlanDependencyTypes.START_START;
        } else if (RelationType.START_FINISH.equals(relation.getType())) {
            return PlanDependencyTypes.START_FINISH;
        }
        return PlanDependencyTypes.FINISH_START;
    }

    private Integer getDurationOfDays(Duration duration){
        if (duration.getUnits().equals(TimeUnit.DAYS)){
            return (int) duration.getDuration();
        }else if (duration.getUnits().equals(TimeUnit.WEEKS)){
            return (int) (duration.getDuration() * 7);
        }else if (duration.getUnits().equals(TimeUnit.MONTHS)){
            return (int) (duration.getDuration() * 30);
        }else if (duration.getUnits().equals(TimeUnit.HOURS)){
            return (int) (duration.getDuration() / 8);
        }
        return 0;
    }
    private List<PlanEntity> buildPlansFromTasks(Long projectId, Long parentId, List<Task> tasks) {
        List<PlanEntity> plans = new ArrayList<>();
        tasks.forEach(task -> {
            String type = PlanTypes.TYPE_TASK;
            if (ObjectUtils.isNotEmpty(task.getChildTasks())) {
                type = PlanTypes.TYPE_GROUP;
            }
            if (task.getMilestone()) {
                type = PlanTypes.TYPE_MILE_STONE;
            }

            PlanEntity plan = PlanEntity.builder().id(IDUtils.getId()).projectId(projectId).name(task.getName())
                                        .type(type).build();
            plan.setPlanStartDate(task.getStart());
            plan.setPlanEndDate(task.getFinish());
            plan.setDuration(getDurationOfDays(task.getDuration()));
            plan.setFinished(false);
            plan.setProgress(0);
            plan.setParentId(parentId);
            plan.setId(IDUtils.getId());
            plan.setOrdinary(task.getID()); //临时使用这个字段存储任务ID
            if (ObjectUtils.isNotEmpty(task.getResourceAssignments())) {
                Resource resource = task.getResourceAssignments().get(0).getResource();
                if (resource!=null) {
                    String resourceName = resource.getName();
                    UserVo user = userService.findOneUserByName(resourceName);
                    if (user != null) {
                        plan.setOwnerId(user.getId());
                    }
                }
            }
            plans.add(plan);

            if (ObjectUtils.isNotEmpty(task.getChildTasks())) {
                plans.addAll(buildPlansFromTasks(projectId, plan.getId(), task.getChildTasks()));
            }

        });
        return plans;
    }

    private void autoPlanFromStart(Collection<Long> ids, Map<Long, PlanVo> taskMap) {
        ids.forEach(id -> {
            PlanVo item = taskMap.get(id);
            LocalDateTime startDate = item.getPlanStartDate();
            LocalDateTime endDate = item.getPlanEndDate();

            item.getPostTasks().forEach(task -> {
                PlanVo postTask = taskMap.get(task.getId());
                if (postTask != null) {
                    //只对未开始的任务进行排期
                    if (!postTask.getFinished() &&
                            (postTask.getProgress() == null || postTask.getProgress() == 0)) {
                        if (PlanDependencyTypes.FINISH_START.equals(task.getLinkType())) {
                            LocalDateTime newEndDate = endDate.plusDays(1);
                            if (postTask.getPlanStartDate() == null ||
                                    newEndDate.isAfter(postTask.getPlanStartDate())) {
                                postTask.setPlanStartDate(newEndDate);
                                postTask.setPlanEndDate(postTask.getPlanStartDate().plusDays(
                                        postTask.getDuration()));
                            }
                        } else if (PlanDependencyTypes.FINISH_FINISH.equals(task.getLinkType())) {
                            LocalDateTime newEndDate = endDate.plusDays(1);
                            if (postTask.getPlanEndDate() == null || newEndDate.isAfter(postTask.getPlanEndDate())) {
                                postTask.setPlanEndDate(newEndDate);
                                postTask.setPlanStartDate(postTask.getPlanEndDate().minusDays(postTask.getDuration()));
                            }
                        } else if (PlanDependencyTypes.START_START.equals(task.getLinkType())) {
                            LocalDateTime newStartDate = startDate.plusDays(1);
                            if (postTask.getPlanStartDate() == null ||
                                    newStartDate.isBefore(postTask.getPlanStartDate())) {
                                postTask.setPlanStartDate(newStartDate);
                                postTask.setPlanEndDate(postTask.getPlanStartDate().plusDays(
                                        postTask.getDuration()));
                            }
                        } else if (PlanDependencyTypes.START_FINISH.equals(task.getLinkType())) {
                            LocalDateTime newStartDate = startDate.plusDays(1);
                            if (postTask.getPlanEndDate() == null || newStartDate.isAfter(postTask.getPlanEndDate())) {
                                postTask.setPlanEndDate(newStartDate);
                                postTask.setPlanStartDate(postTask.getPlanEndDate().minusDays(postTask.getDuration()));
                            }
                        }
                    }


                    autoPlanFromStart(Collections.singletonList(postTask.getId()), taskMap);
                }
            });

        });
    }

    private void calcParent(PlanEntity entity, Collection<PlanEntity> all) {
        Long parentId = entity.getParentId();
        if (ObjectUtils.isNotEmpty(entity.getParentId())) {
            List<PlanEntity> children =
                    all.stream().filter(item -> {return CompareUtils.compare(item.getParentId(), parentId) == 0;})
                       .collect(
                               Collectors.toList());

            PlanEntity parent =
                    all.stream().filter(item -> {return CompareUtils.compare(item.getId(), parentId) == 0;}).findFirst()
                       .get();

            parent.setPlanStartDate(calcPlanStartDateForParent(children));
            parent.setPlanEndDate(calcPlanEndDateForParent(children));
            parent.setProgress(calcProgressForParent(children));
            int progress = parent.getProgress() != null ? parent.getProgress() : 0;
            if (progress < 100 && progress > 0) {
                parent.setFinished(false);
            } else if (progress == 100) {
                parent.setFinished(true);
            }

            calcParent(parent, all);
        }
    }
}
