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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.constants.EnumCodes;
import com.harbortek.helm.tracker.constants.ProjectStatusMeaning;
import com.harbortek.helm.tracker.dao.ProjectDao;
import com.harbortek.helm.tracker.dao.TrackerDao;
import com.harbortek.helm.tracker.dao.TrackerItemDao;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.entity.project.RecentProjectEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.service.ProjectPageService;
import com.harbortek.helm.tracker.service.ProjectRoleMemberService;
import com.harbortek.helm.tracker.service.ProjectService;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.RecentProjectVo;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.template.ProjectTemplateVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.view.ObjectFilter;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.PermissionCacheUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("projectService")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProjectServiceImpl implements ProjectService {
    final ProjectDao projectDao;
    final TrackerDao trackerDao;
    final ProjectRoleMemberService projectRoleMemberService;
    final ProjectPageService projectPageService;
    final TrackerItemDao trackerItemDao;
    final EnumService enumService;

    @Override
    public ProjectVo updateProject(ProjectVo project) {
        ProjectEntity projectEntity = DataUtils.toEntity(project, ProjectEntity.class);
        projectEntity.setOwnerId(project.getOwner().getId());
        projectEntity.setCategoryId(project.getCategory().getId());
        projectEntity.setStatusId(project.getStatus().getId());
        projectDao.updateProject(projectEntity);
        return project;
    }

    @Override
    public void deleteProject(Long projectId) {
        projectDao.deleteProject(projectId);
        PermissionCacheUtils.evictGrantedPermissions(projectId);
    }

    @Override
    public void clearTrackerItemData(Long projectId) {
        List<TrackerEntity> trackerEntities = trackerDao.findByProject(projectId);
        List<Long> trackerIds = trackerEntities.stream().map(BaseEntity::getId).collect(Collectors.toList());
        trackerItemDao.deleteByTrackerIds(trackerIds);
    }

    @Override
    public List<ProjectVo> findAll() {
        List<ProjectEntity> projectEntities = projectDao.findAll();
        return DataUtils.toVo(projectEntities, ProjectVo.class);
    }

    @Override
    public List<ProjectVo> findByOwner(Long ownerId) {
        List<ProjectEntity> projectEntities = projectDao.findProjectsByOwner(ownerId);
        return DataUtils.toVo(projectEntities, ProjectVo.class);
    }

    @Override
    public ProjectTemplateVo saveAsTemplate(ProjectVo project) {
        return null;
    }

    @Override
    public ProjectVo findOneProject(Long id) {
        ProjectEntity projectEntity = projectDao.findOneProject(id);
        ProjectVo projectVo = null;
        if (ObjectUtils.isNotEmpty(projectEntity)) {
            projectVo = DataUtils.toVo(projectEntity, ProjectVo.class);
            // TODO 需要优化性能
            List<ProjectPageVo> pageVos = projectPageService.buildTree(id);
            projectVo.setPages(pageVos);

            List<TrackerEntity> trackerEntities = trackerDao.findByProject(id);
            List<TrackerVo> trackerVos = DataUtils.toVo(trackerEntities, TrackerVo.class);
            projectVo.setTrackers(trackerVos);
        }
        return projectVo;
    }

    @Override
    public Page<ProjectVo> findByCategory(Long categoryId, String keyword, String status, ObjectFilter filter,
            Pageable pageable) {
        Long statusId = null;
        if (StringUtils.isNotEmpty(status)) {
            EnumItemVo statusEntity = enumService.findOneEnumItemByCode(null, EnumCodes.PROJECT_STATUS_MEANING, status);
            if (statusEntity != null) {
                statusId = statusEntity.getId();
            }
        }
        Page<ProjectEntity> projectEntities = projectDao.findByCategory(categoryId, keyword, statusId, filter,
                pageable);

        return DataUtils.toVo(projectEntities, ProjectVo.class);
    }

    @Override
    public Boolean checkDuplicateProjectName(Long projectId, String name) {
        return projectDao.checkDuplicateProjectName(projectId, name);
    }

    @Override
    public Boolean checkDuplicateProjectShortName(Long projectId, String shortName) {
        return projectDao.checkDuplicateProjectShortName(projectId, shortName);
    }

    @Override
    public long countUnFinishedProjects(Long userId) {
        EnumItemVo notStarted = enumService.findOneEnumItemByCode(null, EnumCodes.PROJECT_STATUS_MEANING,
                ProjectStatusMeaning.NOT_STARTED);
        EnumItemVo onGoing = enumService.findOneEnumItemByCode(null, EnumCodes.PROJECT_STATUS_MEANING,
                ProjectStatusMeaning.ONGOING);

        List<Long> projectIds = projectRoleMemberService.findProjectIdsByUserId(userId);
        return projectDao.countUnFinishedProjects(userId, projectIds, Arrays.asList(notStarted.getId(),
                onGoing.getId()));
    }

    @Override
    public List<RecentProjectVo> findRecentProjects(Long userId) {
        List<RecentProjectEntity> projectEntities = projectDao.findRecentProjects(userId, 5L);
        return DataUtils.toVo(projectEntities, RecentProjectVo.class);
    }

    @Override
    public RecentProjectEntity recordRecentProject(Long userId, Long projectId) {
        RecentProjectEntity oneRecentProject = projectDao.findOneRecentProject(userId, projectId);
        if (ObjectUtils.isNotEmpty(oneRecentProject)) {
            oneRecentProject.setLastAccessDate(new Date());
        } else {
            oneRecentProject = RecentProjectEntity.builder().id(IDUtils.getId())
                    .userId(userId).projectId(projectId).lastAccessDate(new Date()).build();
        }
        return projectDao.recordRecentProject(oneRecentProject);
    }
}
