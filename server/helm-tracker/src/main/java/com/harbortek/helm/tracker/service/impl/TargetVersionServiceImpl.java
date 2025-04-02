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

import com.harbortek.helm.tracker.dao.SprintDao;
import com.harbortek.helm.tracker.dao.TargetVersionDao;
import com.harbortek.helm.tracker.dao.TrackerItemDao;
import com.harbortek.helm.tracker.entity.plan.SprintEntity;
import com.harbortek.helm.tracker.entity.plan.TargetVersionEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.service.TargetVersionService;
import com.harbortek.helm.tracker.vo.plan.TargetVersionVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.DateUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service("targetVersionService")
@Slf4j
public class TargetVersionServiceImpl implements TargetVersionService {

    @Autowired
    TargetVersionDao versionDao;

    @Autowired
    SprintDao sprintDao;

    @Autowired
    TrackerItemDao trackerItemDao;


    @Override
    public Collection<TargetVersionVo> findTargetVersions(Long projectId) {
        return DataUtils.toVo(versionDao.findAll(projectId), TargetVersionVo.class);
    }

    @Override
    public TargetVersionVo findOneTargetVersion(Long id) {
        return DataUtils.toVo(versionDao.findById(id, TargetVersionEntity.class), TargetVersionVo.class);
    }

    @Override
    public TargetVersionVo createTargetVersion(TargetVersionVo versionVo) {
        TargetVersionEntity entity = DataUtils.toEntity(versionVo, TargetVersionEntity.class);
        entity = versionDao.createVersion(entity);
        return DataUtils.toVo(entity, TargetVersionVo.class);
    }

    @Override
    public TargetVersionVo updateTargetVersion(TargetVersionVo versionVo) {
        TargetVersionEntity entity = DataUtils.toEntity(versionVo, TargetVersionEntity.class);
        entity = versionDao.updateVersion(entity);
        return DataUtils.toVo(entity, TargetVersionVo.class);
    }

    @Override
    public void deleteTargetVersion(Long id) {
        versionDao.deleteVersion(id);
    }

    @Override
    public void syncTargetVersions(Long projectId) {
        List<TargetVersionVo> targetVersions = DataUtils.toVo(versionDao.findAll(projectId), TargetVersionVo.class);
        if (ObjectUtils.isNotEmpty(targetVersions)){
            for (TargetVersionVo version : targetVersions) {
                List<SprintEntity> sprintEntities = sprintDao.findSprintByTargetVersion(version.getProjectId(),version.getId());
                if (ObjectUtils.isNotEmpty(sprintEntities)){
                    Date planStartDate = null;
                    Date planEndDate = null;
                    Date realStartDate = null;
                    Date realEndDate = null;

                    for (SprintEntity sprintEntity : sprintEntities) {
                        if (ObjectUtils.isEmpty(planStartDate) || ObjectUtils.isNotEmpty(sprintEntity.getPlanStartDate()) && DateUtils.isBefore(sprintEntity.getPlanStartDate(),planStartDate)){
                            planStartDate = sprintEntity.getPlanStartDate();
                        }
                        if (ObjectUtils.isEmpty(planEndDate) || ObjectUtils.isNotEmpty(sprintEntity.getPlanEndDate()) && DateUtils.isAfter(sprintEntity.getPlanEndDate(),planEndDate)){
                            planEndDate = sprintEntity.getPlanEndDate();
                        }

                        if (ObjectUtils.isEmpty(realStartDate) || ObjectUtils.isNotEmpty(sprintEntity.getRealStartDate()) && DateUtils.isBefore(sprintEntity.getRealStartDate(),realStartDate)){
                            realStartDate = sprintEntity.getRealStartDate();
                        }
                        if (ObjectUtils.isEmpty(realEndDate) || ObjectUtils.isNotEmpty(sprintEntity.getRealEndDate()) && DateUtils.isAfter(sprintEntity.getRealEndDate(),realEndDate)){
                            realEndDate = sprintEntity.getRealEndDate();
                        }
                    }
                    version.setPlanStartDate(planStartDate);
                    version.setPlanEndDate(planEndDate);
                    version.setRealStartDate(realStartDate);
                    version.setRealEndDate(realEndDate);
                    List<Long> sprintIds = sprintEntities.stream().map(SprintEntity::getId).toList();
                    List<TrackerItemEntity> items = trackerItemDao.findBySprintIds(projectId, sprintIds);
                    if (ObjectUtils.isNotEmpty(items)){
                        Double totalWorkingHours = items.stream()
                            .map(TrackerItemEntity::getEstimateWorkingHours)
                            .filter(Objects::nonNull)  // 过滤掉null值
                            .reduce(0.0, Double::sum);
                        Double completedWorkingHours = items.stream()
                            .map(TrackerItemEntity::getRegisteredWorkingHours)
                            .filter(Objects::nonNull)  // 过滤掉null值
                            .reduce(0.0, Double::sum);
                        Double remainingWorkingHours = items.stream()
                            .map(TrackerItemEntity::getRemainingWorkingHours)
                            .filter(Objects::nonNull)  // 过滤掉null值
                            .reduce(0.0, Double::sum);
                        version.setTotalWorkingHours(totalWorkingHours);
                        version.setCompletedWorkingHours(completedWorkingHours);
                        version.setRemainingWorkingHours(remainingWorkingHours);
                    }
                }
                versionDao.updateVersion(DataUtils.toEntity(version, TargetVersionEntity.class));
            }
        }
    }
}
