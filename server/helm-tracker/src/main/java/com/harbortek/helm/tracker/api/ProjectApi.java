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

package com.harbortek.helm.tracker.api;

import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.constants.EnumCodes;
import com.harbortek.helm.tracker.constants.ProjectStatusMeaning;
import com.harbortek.helm.tracker.entity.project.RecentProjectEntity;
import com.harbortek.helm.tracker.service.ProjectCreateService;
import com.harbortek.helm.tracker.service.ProjectPermissionService;
import com.harbortek.helm.tracker.service.ProjectService;
import com.harbortek.helm.tracker.vo.ProjectCopyVo;
import com.harbortek.helm.tracker.vo.ProjectListVo;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.RecentProjectVo;
import com.harbortek.helm.tracker.vo.view.ObjectFilter;
import com.harbortek.helm.tracker.vo.view.TrackerOrderBy;
import com.harbortek.helm.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.harbortek.helm.tracker.constants.ProjectPermissions.ALL_PERMISSIONS;

@Controller
@Tag(name = "项目管理")
@RequestMapping(value = "/tracker/project")
public class ProjectApi {
	@Autowired
	ProjectService projectService;

	@Autowired
	ProjectCreateService projectCreateService;

	@Autowired
	EnumService enumService;
	@Autowired
	ProjectPermissionService projectPermissionService;

	@Parameter(name="查询项目分类列表")
	@RequestMapping(value = "/categories", method = RequestMethod.GET)
	ResponseEntity<List<EnumItemVo>> findProjectCategories() {
		List<EnumItemVo> projectCategories = enumService.findEnumItemsByCode(null, EnumCodes.PROJECT_CATEGORY);
		return ResponseEntity.ok(projectCategories);
	}

	@Parameter(name="查询项目列表")
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	ResponseEntity<Page<ProjectVo>> findProjects(@RequestParam(value = "categoryId", required = false) Long categoryId,
												 @RequestParam(value = "keyword", required = false) String keyword,
												 @RequestParam(value = "status", required = false) String status,
												 @RequestBody(required = false) ObjectFilter filter,
												 @RequestBody(required = false) TrackerOrderBy orderBy,
												 Pageable pageable) {
		Page<ProjectVo> projectVos = projectService.findByCategory(categoryId, keyword, status,filter, pageable);
		return ResponseEntity.ok(projectVos);
	}

	@Parameter(name="查询一个项目")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	ResponseEntity<ProjectVo> findOneProject(@PathVariable Long id) {
		ProjectVo project = projectService.findOneProject(id);
		return ResponseEntity.ok(project);
	}

	@Parameter(name="创建一个项目")
	@RequestMapping(value = "", method = RequestMethod.POST)
	ResponseEntity<ProjectVo> createProject(@RequestBody ProjectVo project) {
		ProjectVo result = projectCreateService.createProject(project);
		return ResponseEntity.ok(result);
	}

	@Parameter(name="复制一个项目")
	@RequestMapping(value = "copyProject", method = RequestMethod.POST)
	ResponseEntity<ProjectVo> copyProject(@RequestBody ProjectCopyVo projectCopyVo) {
		ProjectVo result = projectCreateService.copyProject(projectCopyVo);
		return ResponseEntity.ok(result);
	}

	@Parameter(name="更新一个项目")
	@RequestMapping(value = "", method = RequestMethod.PUT)
	ResponseEntity<ProjectVo> updateProject(@RequestBody ProjectVo project) {
		ProjectVo result = projectService.updateProject(project);
		return ResponseEntity.ok(result);
	}

	@Parameter(name="删除一个项目")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	ResponseEntity deleteProject(@PathVariable Long id) {
		projectService.deleteProject(id);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="清除所有工作项数据")
	@RequestMapping(value = "/clear-item/{id}", method = RequestMethod.DELETE)
	ResponseEntity clearTrackerItemData(@PathVariable Long id) {
		projectService.clearTrackerItemData(id);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="查询项目名称是否已重复")
	@RequestMapping(value = "/checkDuplicateProjectName", method = RequestMethod.GET)
	ResponseEntity<Boolean> checkDuplicateProjectName(@RequestParam(value = "id", required = false) Long projectId, @RequestParam(value = "name", required = true) String name) {
		boolean result = projectService.checkDuplicateProjectName(projectId, name);
		return ResponseEntity.ok(result);
	}

	@Parameter(name="查询项目缩略名称是否已重复")
	@RequestMapping(value = "/checkDuplicateProjectShortName", method = RequestMethod.GET)
	ResponseEntity<Boolean> checkDuplicateProjectShortName(@RequestParam(value = "id", required = false) Long projectId, @RequestParam(value = "shortName", required = true) String shortName) {
		Boolean result = projectService.checkDuplicateProjectShortName(projectId, shortName);
		return ResponseEntity.ok(result);
	}

	@Parameter(name="查询按项目状态组织的最新项目列表")
	@RequestMapping(value = "/notEndedProjects", method = RequestMethod.GET)
	ResponseEntity<ProjectListVo> findNotEndedProjects(@RequestParam(value = "keyword", required = false) String keyword) {
		Page<ProjectVo> notStartedProjects = projectService.findByCategory(null, keyword, ProjectStatusMeaning.NOT_STARTED,null, Pageable.ofSize(10));
		Page<ProjectVo> onGoingProjects = projectService.findByCategory(null, keyword, ProjectStatusMeaning.ONGOING,null, Pageable.ofSize(10));
		long totalCount = projectService.countUnFinishedProjects(SecurityUtils.getCurrentUser().getId());
		ProjectListVo vo = new ProjectListVo();
		vo.setNotStartedProjects(notStartedProjects.getContent());
		vo.setOnGoingProjects(onGoingProjects.getContent());
		vo.setTotalCount(totalCount);
		return ResponseEntity.ok(vo);
	}

//	@Parameter(name="查询最近打开的项目列表")
//	@RequestMapping(value = "/recentProjects", method = RequestMethod.GET)
//	ResponseEntity<ProjectListVo> recentProjects() {
//		List<ProjectVo> projects = projectService.findRecentProjects(SecurityUtils.getCurrentUser().getId());
//		long totalCount = projectService.countUnFinishedProjects(SecurityUtils.getCurrentUser().getId());
//		ProjectListVo vo = new ProjectListVo();
//		vo.setRecentProjects(projects);
//		vo.setTotalCount(totalCount);
//		return ResponseEntity.ok(vo);
//	}
	@Parameter(name="查询最近打开的项目列表")
	@RequestMapping(value = "/recentProjects", method = RequestMethod.GET)
	ResponseEntity<ProjectListVo> recentProjects(@RequestParam(required = false) String keyword) {
		Long userId = SecurityUtils.getCurrentUser().getId();
		List<RecentProjectVo> projects = projectService.findRecentProjects(userId);
		long totalCount = projectService.countUnFinishedProjects(userId);
		ProjectListVo vo = new ProjectListVo();
		vo.setRecentProjects(projects);
		vo.setTotalCount(totalCount);
		return ResponseEntity.ok(vo);
	}

	@Parameter(name="记录最近项目")
	@RequestMapping(value = "/recordProject", method = RequestMethod.GET)
	ResponseEntity<RecentProjectEntity> recordRecentProject(@RequestParam Long projectId) {
		Long userId = SecurityUtils.getCurrentUser().getId();
		RecentProjectEntity recentProject = projectService.recordRecentProject(userId, projectId);
		return ResponseEntity.ok(recentProject);
	}

	@Parameter(name="查询所有项目权限定义")
	@RequestMapping(value = "/permissions", method = RequestMethod.GET)
	ResponseEntity<List<String>> findPermissions() {
		List<String> permissions = ALL_PERMISSIONS;
		return ResponseEntity.ok(permissions);
	}

}
