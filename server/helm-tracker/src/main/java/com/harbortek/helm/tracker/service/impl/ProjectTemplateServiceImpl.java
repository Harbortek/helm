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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.common.vo.IdNameVo;
import com.harbortek.helm.system.dao.EnumCategoryDao;
import com.harbortek.helm.system.dao.EnumDao;
import com.harbortek.helm.system.entity.EnumCategoryEntity;
import com.harbortek.helm.system.entity.EnumItemEntity;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.constants.InternalTrackers;
import com.harbortek.helm.tracker.dao.ProjectDao;
import com.harbortek.helm.tracker.dao.ProjectTemplateDao;
import com.harbortek.helm.tracker.dao.TrackerLinkDao;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.service.DocService;
import com.harbortek.helm.tracker.service.ProjectPageService;
import com.harbortek.helm.tracker.service.ProjectPermissionService;
import com.harbortek.helm.tracker.service.ProjectRoleMemberService;
import com.harbortek.helm.tracker.service.ProjectTemplateService;
import com.harbortek.helm.tracker.service.SmartPageService;
import com.harbortek.helm.tracker.service.TrackerItemService;
import com.harbortek.helm.tracker.service.TrackerService;
import com.harbortek.helm.tracker.template.reader.ProjectTemplateReader;
import com.harbortek.helm.tracker.template.writer.ProjectTemplateWriter;
import com.harbortek.helm.tracker.util.ResourceUtils;
import com.harbortek.helm.tracker.vo.block.DocVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkVo;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.smartpage.SmartPageVo;
import com.harbortek.helm.tracker.vo.template.ProjectTemplateVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.MultiOptionsField;
import com.harbortek.helm.tracker.vo.tracker.fields.OptionsField;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectTemplateServiceImpl implements ProjectTemplateService, ApplicationContextAware {
	private final Logger logger = LoggerFactory.getLogger(ProjectTemplateServiceImpl.class);
	private ApplicationContext applicationContext;
	@Autowired
	ProjectTemplateDao projectTemplateDao;

	@Autowired
	ProjectTemplateReader templateReader;

	@Autowired
	ProjectTemplateWriter templateWriter;

	@Autowired
	ProjectDao projectDao;

	@Autowired
	TrackerItemService trackerItemService;

	@Autowired
	RoleService roleService;

	@Autowired
	ProjectRoleMemberService projectRoleMemberService;

	@Autowired
	ProjectPermissionService projectPermissionService;
	@Autowired
	ProjectPageService projectPageService;
	@Autowired
	TrackerService trackerService;
	@Autowired
	TrackerLinkDao trackerLinkDao;
	@Autowired
	DocService docService;
	@Autowired
	EnumCategoryDao enumCategoryDao;
	@Autowired
	EnumDao enumDao;
	@Autowired
	SmartPageService smartPageService;

	@Value("${HELM_HOME}")
	private String helmHome;

	private final static String PROJECT_TEMPLATE_PATH = "templates";

	@Override
	public ProjectTemplateVo createProjectTemplate(ProjectTemplateVo template) {
		templateWriter.writeFully(template);
		return null;
	}

	@Override
	public List<ProjectTemplateVo> findAll() {
		List<ProjectTemplateVo> templateVoList = new ArrayList<>();
		try {
			String templatePath = FilenameUtils.normalizeNoEndSeparator(helmHome) + File.separator
					+ FilenameUtils.normalize(PROJECT_TEMPLATE_PATH);
			Resource resource = new FileSystemResource(templatePath);
			File[] files = resource.getFile().listFiles();
			if (files == null) {
				return templateVoList;
			}
			for (File file : files) {
				ProjectTemplateVo templateVo = templateReader.readTemplateBasicInfo(new FileSystemResource(file));
				if (templateVo == null) {
					continue;
				}
				templateVoList.add(templateVo);
			}
		} catch (IOException ex) {
			logger.error("IOException", ex);
		}
		return templateVoList;
	}

	@Override
	public ProjectTemplateVo findByName(String name) {
		List<ProjectTemplateVo> templateVoList = findAll();
		Optional<ProjectTemplateVo> templateVo = templateVoList.stream().filter(t -> {
			return t.getName().equals(name);
		}).findFirst();

		if (templateVo.isPresent()) {
			Resource resource = ResourceUtils.getResource(templateVo.get().getLocation());
            return templateReader.readFully(resource);
		}
		return null;
	}

	@Override
	public ProjectTemplateVo findDataByProjectId(Long projectId) {
		ProjectTemplateVo templateVo = new ProjectTemplateVo();

		templateVo.setRoles(projectRoleMemberService.findProjectRoleMembers(projectId, false));
		templateVo.setPermissionGrants(projectPermissionService.findPermissionGrantList(projectId));

		List<EnumItemEntity> enumItemEntities = enumDao.findEnumsByProjectId(projectId);
		List<EnumItemVo> enumItemVos = DataUtils.toVo(enumItemEntities, EnumItemVo.class);
		Map<Long, String> enumCategoryMap = enumCategoryDao.findEnumCategories(null, projectId, null).getContent()
				.stream()
				.collect(Collectors.toMap(EnumCategoryEntity::getId, EnumCategoryEntity::getName));
		enumItemVos.forEach(enumItemVo -> {
			enumItemVo.setCategoryName(enumCategoryMap.get(enumItemVo.getCategoryId()));
		});
		templateVo.setEnumItems(enumItemVos);

		List<TrackerVo> trackerVos = trackerService.findByProject(projectId, false);
		trackerVos.forEach(tracker -> {
			tracker.getTrackerFields().forEach(field -> {
				if (!field.getSystem() && field instanceof OptionsField) {
					((OptionsField) field).setEnumName(enumCategoryMap.get(((OptionsField) field).getEnumId()));
				} else if (!field.getSystem() && field instanceof MultiOptionsField) {
					((MultiOptionsField) field)
							.setEnumName(enumCategoryMap.get(((MultiOptionsField) field).getEnumId()));
				}
			});
			tracker.setTrackerPermissions(trackerService.findTrackerPermissions(tracker.getId()));
		});
		templateVo.setTrackers(trackerVos);

		List<TrackerItemVo> internalItems = trackerItemService.findByTrackerIds(projectId,
				Arrays.asList(InternalTrackers.HEADING.getId(),
						InternalTrackers.PARAGRAPH.getId(), InternalTrackers.TITLE.getId()));
		List<TrackerItemVo> trackerItemVos = trackerItemService
				.findTrackerItems(projectId, null, null, null, null, null, Pageable.unpaged()).getContent();
		internalItems.addAll(trackerItemVos);
		templateVo.setTrackerItems(internalItems);

		templateVo.setPages(projectPageService.buildTree(projectId));
		List<Long> itemIds = templateVo.getTrackerItems().stream().map(IdNameVo::getId).collect(Collectors.toList());
		templateVo.setTrackerLinks(DataUtils.toVo(trackerLinkDao.findByItemIds(itemIds), TrackerLinkVo.class));

		List<ProjectPageVo> pageVos = projectPageService.findByProjectId(projectId);
		List<Long> docIds = pageVos.stream().map(ProjectPageVo::getSmartDocId)
				.distinct().filter(Objects::nonNull).toList();
		List<DocVo> docVos = docService.findDocByIds(docIds);
		templateVo.setDocs(docVos);

		List<Long> smartPageIds = pageVos.stream().map(ProjectPageVo::getSmartPageId)
				.distinct().filter(Objects::nonNull).toList();

		List<SmartPageVo> smartPages = smartPageService.findSmartPageByIds(smartPageIds);
		templateVo.setSmartPages(smartPages);
		return templateVo;
	}

	@Override
	public void saveAsTemplate(BaseEntity template) {
		if (ObjectUtils.isEmpty(template.getId())) {
			throw new RuntimeException("模板保存失败");
		}
		ProjectTemplateVo templateVo = findDataByProjectId(template.getId());
		ProjectEntity oneProject = projectDao.findOneProject(template.getId());

		templateVo.setName(template.getName());
		templateVo.setDescription(template.getDescription());
		templateVo.setMaxItemNo(oneProject.getMaxItemNo());
		templateVo.setIcon(oneProject.getIcon());
		//		templateVo.setTrackerPrefixToReplace(oneProject.getKeyName());

		createProjectTemplate(templateVo);

	}

	@Override
	public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}

