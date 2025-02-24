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

import com.harbortek.helm.tracker.entity.project.RecentProjectEntity;
import com.harbortek.helm.tracker.vo.ProjectCopyVo;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.RecentProjectVo;
import com.harbortek.helm.tracker.vo.template.ProjectTemplateVo;
import com.harbortek.helm.tracker.vo.view.ObjectFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface ProjectService {
//	ProjectVo createProject(ProjectVo project);

//	ProjectVo copyProject(ProjectCopyVo projectCopyVo);

	ProjectVo updateProject(ProjectVo project);

	void deleteProject(Long id);

	List<ProjectVo> findAll();

	List<ProjectVo> findByOwner(Long ownerId);

//	Collection<ProjectVo> findByWorkspace(WorkSpace workSpace);
//
//	Collection<ProjectVo> findByProjectSet(ProjectSetEntity projectSet);

	ProjectTemplateVo saveAsTemplate(ProjectVo project);

	ProjectVo findOneProject(Long id);

	Page<ProjectVo> findByCategory(Long categoryId, String keyword, String status, ObjectFilter filter, Pageable pageable);

	Boolean checkDuplicateProjectName(Long projectId, String name);

	Boolean checkDuplicateProjectShortName(Long projectId, String shortName);

	long countUnFinishedProjects(Long userId);

	void clearTrackerItemData(Long id);

	List<RecentProjectVo> findRecentProjects(Long userId);

	RecentProjectEntity recordRecentProject(Long userId, Long projectId);

}
