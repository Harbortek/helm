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

import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.tracker.service.ProjectTemplateService;
import com.harbortek.helm.tracker.vo.template.ProjectTemplateVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@Tag(name = "项目管理")
@RequestMapping(value = "/tracker/project/template")
public class ProjectTemplateApi {
	@Autowired
	ProjectTemplateService projectTemplateService;

	@Parameter(name="查询项目模版列表")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	ResponseEntity<List<ProjectTemplateVo>> findProjectTemplates(){
		List<ProjectTemplateVo> templates = projectTemplateService.findAll();
		return ResponseEntity.ok(templates);
	}

	@Parameter(name="保存为项目模板接口")
	@RequestMapping(value = "/saveAsTemplate", method = RequestMethod.POST)
	ResponseEntity<Void> saveAsTemplate(@RequestBody BaseEntity template){
		 projectTemplateService.saveAsTemplate(template);
		return ResponseEntity.ok().build();
	}

//	@Parameter(name="查询一个项目模版")
//	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
//	ResponseEntity<IdNameVo> findOneProjectTemplate(@PathVariable Long id){
//		IdNameVo template = dataUtils.toVo(projectTemplateService.findById(id), IdNameVo.class);
//		return ResponseEntity.ok(template);
//	}

//	@Parameter(name="查询一个项目模版下的角色")
//	@RequestMapping(value = "/{id}/roles", method = RequestMethod.GET)
//	ResponseEntity<List<ProjectRoleMemberVo>> findRoleMembersByTemplate(@PathVariable Long id){
//		ProjectTemplateEntity template = projectTemplateService.findById(id);
//
//		TemplateXmlReader templateXmlReader = new TemplateXmlReader(new StringReader(template.getContent()));
//		ProjectTemplateVo template2 = templateXmlReader.buildTemplate();
//		List<ProjectRoleMemberVo> roleMemberVos = template2.getRoles();
//		//只展示明确为角色的数据，隐藏角色不展示
//		roleMemberVos = roleMemberVos.stream().filter(r->{return !r.getSpecialRole();}).collect(Collectors.toList());
//		List<ProjectRoleMemberVo> result = dataUtils.toVo(roleMemberVos, com.harbortek.helm.tracker.vo.ProjectRoleMemberVo.class);
//		//自动分配ID，避免数据混乱，默认把当前用户作为项目管理员
//		result.forEach(projectRoleMemberVo -> {
//			projectRoleMemberVo.setId(IDUtils.getId());
//			if ("Project Admin".equals(projectRoleMemberVo.getName())){
//				UserEntity currentUser = SecurityUtils.getCurrentUser();
//				UserVo user = new UserVo();
//				user.setId(currentUser.getId());
//				user.setName(currentUser.getName());
//
//				projectRoleMemberVo.getMembers().add( user );
//			}
//		});
//
//		return ResponseEntity.ok(result);
//	}
}
