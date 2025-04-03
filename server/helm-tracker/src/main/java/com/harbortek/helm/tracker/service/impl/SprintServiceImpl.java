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

import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.common.vo.IdNameVo;
import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.tracker.constants.EnumCodes;
import com.harbortek.helm.tracker.constants.ProjectStatusMeaning;
import com.harbortek.helm.tracker.dao.ProjectDao;
import com.harbortek.helm.tracker.dao.SprintDao;
import com.harbortek.helm.tracker.dao.TrackerDao;
import com.harbortek.helm.tracker.dao.TrackerItemDao;
import com.harbortek.helm.tracker.entity.plan.SprintEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.service.SprintService;
import com.harbortek.helm.tracker.service.TargetVersionService;
import com.harbortek.helm.tracker.service.TrackerItemService;
import com.harbortek.helm.tracker.vo.plan.SprintVo;
import com.harbortek.helm.tracker.vo.tracker.fields.SprintField;
import com.harbortek.helm.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("sprintService")
@Slf4j
public class SprintServiceImpl implements SprintService {
    @Autowired
    SprintDao sprintDao;

    @Autowired
    TrackerItemDao trackerItemDao;

    @Autowired
    ProjectDao projectDao;

    @Autowired
    EnumService enumService;

    @Autowired
    TrackerItemService trackerItemService;

    @Autowired
    TrackerDao trackerDao;

    @Autowired
    TargetVersionService targetVersionService;


    @Override
    public Collection<SprintVo> findSprints(Long projectId) {
        if (projectId == null) {
            return null;
        }
        List<SprintEntity> sprintEntities = sprintDao.findSprints(projectId);
        List<SprintVo> sprintVos = DataUtils.toVo(sprintEntities, SprintVo.class);
        //计算迭代进度
        List<Long> sprintIds = sprintEntities.stream().map(BaseEntity::getId).collect(Collectors.toList());
        List<TrackerItemEntity> trackerItemList = trackerItemDao.findBySprintIds(projectId, sprintIds);
        if (ObjectUtils.isNotEmpty(trackerItemList)) {
            List<Long> itemIds = trackerItemList.stream().map(TrackerItemEntity::getTrackerId).toList();
            Map<Long, Long> trackerMap = trackerDao.findByIds(itemIds, TrackerEntity.class)
                    .stream().collect(Collectors.toMap(BaseEntity::getId, tracker ->
                            tracker.getTrackerFields().stream()
                                    .filter(field -> field instanceof SprintField && !field.getSystem())
                                    .findFirst()
                                    .map(IdNameVo::getId)
                                    .orElse(0L)
                    ));

            sprintVos.forEach(sprintVo -> {
                double progress = 0, duration = 0, progressTotal = 0, durationTotal = 0;
                for (TrackerItemEntity trackerItem : trackerItemList) {
                    Long itemSprintId = trackerMap.get(trackerItem.getTrackerId());
                    if (itemSprintId.equals(sprintVo.getId())) {
                        progress = 0;
                        duration = 1;
                        if (trackerItem.getPlanStartDate() != null && trackerItem.getPlanEndDate() != null) {
                            duration = DateUtils.daysBetween(trackerItem.getPlanStartDate(), trackerItem.getPlanEndDate());
                        }
                        if (trackerItem.getProgress() != null && trackerItem.getProgress() != 0) {
                            progress = trackerItem.getProgress();
                        }
                        progressTotal += (duration * progress);
                        durationTotal += duration;
                    }
                }
                if (progressTotal == 0) {
                    sprintVo.setProgress(0);
                } else {
                    sprintVo.setProgress(Integer.parseInt(String.format("%.0f", (progressTotal / durationTotal))));
                }
            });
        }
        return sprintVos;
    }

    @Override
    public SprintVo findOneSprint(Long id) {
        SprintEntity sprint = sprintDao.findById(id);
        return DataUtils.toVo(sprint, SprintVo.class);
    }

    @Override
    public SprintVo createSprint(SprintVo sprintVo) {
        sprintVo.setId(IDUtils.getId());
        SprintEntity entity = DataUtils.toEntity(sprintVo, SprintEntity.class);
        entity.setItemNo(projectDao.getNextItemNo(entity.getProjectId()));
        entity.setStatusId(enumService.findOneEnumItemByCode(sprintVo.getProjectId(), EnumCodes.PROJECT_STATUS_MEANING,
                ProjectStatusMeaning.NOT_STARTED).getId());
        entity.setMeaning(ProjectStatusMeaning.NOT_STARTED);
        entity = sprintDao.createSprint(entity);
        return DataUtils.toVo(entity, SprintVo.class);
    }


    @Override
    public SprintVo updateSprint(SprintVo sprintVo) {
        SprintEntity entity = DataUtils.toEntity(sprintVo, SprintEntity.class);
        entity.setStatusId(sprintVo.getStatus().getId());
        entity.setOwnerId(sprintVo.getOwner().getId());
        entity.setTargetVersionId(sprintVo.getTargetVersion().getId());
        entity.setPlanStartDate(sprintVo.getPlanStartDate());
        entity.setPlanEndDate(sprintVo.getPlanEndDate());
        sprintDao.updateSprint(entity);
        return DataUtils.toVo(entity, SprintVo.class);
    }

    @Override
    public void deleteSprint(Long id) {
        //修改迭代下的item
        Long projectId = (Long) SecurityUtils.get(SecurityUtils.PROJECT_ID);
        List<Long> itemIds = trackerItemDao.findBySprintIds(projectId, List.of(id)).stream().map(TrackerItemEntity::getId).toList();
        trackerItemService.updateTrackerItemSprint(null, itemIds);

        //删除迭代
        sprintDao.deleteSprint(id);
    }

    @Override
    public SprintVo convertSprint(SprintVo sprintVo, String undone, Long sprintId) {
        SprintEntity entity = DataUtils.toEntity(sprintVo, SprintEntity.class);
        if(ObjectUtils.isNotEmpty(undone)){
            List<Long> itemIds = trackerItemService.findItemIdsBySprint(entity.getId());
            if("noPlan".equals(undone)){
                trackerItemService.updateTrackerItemSprint(null, itemIds);
            }else{
                trackerItemService.updateTrackerItemSprint(sprintId, itemIds);
            }
        }

        if(ProjectStatusMeaning.NOT_STARTED.equals(sprintVo.getMeaning())){
            entity.setStatusId(enumService.findOneEnumItemByCode(sprintVo.getProjectId(), EnumCodes.PROJECT_STATUS_MEANING,
                    ProjectStatusMeaning.ONGOING).getId());
            entity.setMeaning(ProjectStatusMeaning.ONGOING);
        } else if (ProjectStatusMeaning.ONGOING.equals(sprintVo.getMeaning())) {
            entity.setStatusId(enumService.findOneEnumItemByCode(sprintVo.getProjectId(), EnumCodes.PROJECT_STATUS_MEANING,
                    ProjectStatusMeaning.ENDED).getId());
            entity.setMeaning(ProjectStatusMeaning.ENDED);
        }
        entity.setRealStartDate(sprintVo.getRealStartDate());
        entity.setRealEndDate(sprintVo.getRealEndDate());
        entity = sprintDao.createSprint(entity);
        return DataUtils.toVo(entity, SprintVo.class);
    }

    @Override
    public Collection<SprintVo> findUnPlanedSprints(Long projectId) {
        List<SprintEntity> sprintEntities = sprintDao.findUnPlanedSprints(projectId);
        return DataUtils.toVo(sprintEntities, SprintVo.class);
    }

    @Override
    public void syncSprintWorkingHours(Long projectId, Long sprintId) {
        SprintEntity sprintEntity = sprintDao.findById(sprintId);
        List<TrackerItemEntity> trackerItemEntities = trackerItemDao.findBySprintIds(projectId, List.of(sprintId));
        if (ObjectUtils.isNotEmpty(trackerItemEntities)){
            Double totalWorkingHours = trackerItemEntities.stream()
                    .map(TrackerItemEntity::getEstimateWorkingHours)
                    .filter(Objects::nonNull)  // 过滤掉null值
                    .reduce(0.0, Double::sum);
            Double completedWorkingHours = trackerItemEntities.stream()
                    .map(TrackerItemEntity::getRegisteredWorkingHours)
                    .filter(Objects::nonNull)  // 过滤掉null值
                    .reduce(0.0, Double::sum);
            Double remainingWorkingHours = trackerItemEntities.stream()
                    .map(TrackerItemEntity::getRemainingWorkingHours)
                    .filter(Objects::nonNull)  // 过滤掉null值
                    .reduce(0.0, Double::sum);
            sprintEntity.setTotalWorkingHours(totalWorkingHours);
            sprintEntity.setCompletedWorkingHours(completedWorkingHours);
            sprintEntity.setRemainingWorkingHours(remainingWorkingHours);
        }
        sprintDao.updateSprint(sprintEntity);

        if (ObjectUtils.isNotEmpty(sprintEntity.getTargetVersionId())){
            targetVersionService.syncTargetVersions(projectId);
        }
    }

}
