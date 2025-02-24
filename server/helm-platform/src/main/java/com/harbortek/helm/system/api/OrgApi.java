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

package com.harbortek.helm.system.api;

import com.harbortek.helm.common.annotation.PermissionCheck;
import com.harbortek.helm.system.config.SystemModulePermissions;
import com.harbortek.helm.system.service.OrgService;
import com.harbortek.helm.system.vo.OrgVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.MenuTreeUtil;
import com.harbortek.helm.util.excel.ExcelLogs;
import com.harbortek.helm.util.excel.ExcelUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
@Tag(name = "系统管理-组织机构", description = "组织机构API")
@RequestMapping(value = "/sys/org")
public class OrgApi {

	@Autowired
	OrgService orgService;

	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_DEPT)
	@Parameter(name= "增加机构")

	@RequestMapping(value = "create",
			produces = {"application/json"},
			consumes = {"application/json"},
			method = RequestMethod.POST)
	public ResponseEntity<Void> createOrg(HttpServletRequest request, @RequestBody OrgVo body) {

		if (body.getId() == null) {
			body.setId(IDUtils.getId());
		}
		if (body.getParentId() == null) {
			body.setParentId(0L);
		}
		orgService.createOrganiztion(body);

		return ResponseEntity.ok().build();
	}

	@Parameter(name= "删除机构")

	@RequestMapping(value = "delete/{orgId}",
			produces = {"application/json"},
			method = RequestMethod.POST)
	public ResponseEntity<Boolean> deleteOrg(@PathVariable(name = "orgId") Long orgId) {

		boolean result = orgService.deleteOrganization(orgId);

		return ResponseEntity.ok(result);
	}

	//	@PermissionCheck(code = SystemPermissions.SYSTEM_ORG_MANAGE)
	@Parameter(name= "修改机构")
	@RequestMapping(value = "update",
			produces = {"application/json"},
			consumes = {"application/json"},
			method = RequestMethod.POST)
	public ResponseEntity<Void> updateOrg(
			@RequestBody OrgVo body) {
		OrgVo org = orgService.findOneOrganization(body.getId());
		orgService.updateOrganiztion(body);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}


	@Parameter(name= "根据Id查询机构")
	@RequestMapping(value = "findById/{orgId}",
			produces = {"application/json"},
			method = RequestMethod.GET)
	public ResponseEntity<OrgVo> findOrgById(
			@PathVariable(name = "orgId") Long orgId) {
		OrgVo org = orgService.findOneOrganization(orgId);
		return ResponseEntity.ok(org);
	}


	@Parameter(name= "查询机构列表")
	@RequestMapping(value = "findAll",
			produces = {"application/json"},
			method = RequestMethod.GET)
	public ResponseEntity<Page> findOrganizations(String keyword, Pageable pageable) {
		Page<OrgVo> data = orgService.findOrganizations(keyword, pageable);
		return ResponseEntity.ok(data);
	}

	@Parameter(name= "查询机构树形列表")
	@RequestMapping(value = "/findOrganizationTree", method = RequestMethod.GET)
	public ResponseEntity<List> findOrganizationTree() {
		Page<OrgVo> data = orgService.findOrganizations("", Pageable.unpaged());
		List<Object> list = MenuTreeUtil.orgList(data.getContent());
		return ResponseEntity.ok(list);
	}

	@Parameter(name= "查询机构树形列表带科室数量")
	@RequestMapping(value = "/findOrganizationTreeWithCount",
			method = RequestMethod.GET)

	public ResponseEntity<List> findOrganizationTreeWithCount() {
		Page<OrgVo> data = orgService.findOrganizationsWithCount(Pageable.unpaged());
		List<Object> list = MenuTreeUtil.orgList(data.getContent());
		return ResponseEntity.ok(list);
	}


	//	@PermissionCheck(code = SystemPermissions.SYSTEM_ORG_MANAGE)
	@Parameter(name= "导入组织预检查")
	@RequestMapping(value = "importcheck",
			method = RequestMethod.POST)
	public ResponseEntity<ExcelLogs> importOrganizationsPreCheck(
			@RequestParam("uploadFile") String uploadFile) {
		ExcelLogs logs = orgService.importOrganizationsPreCheck(uploadFile);

		return ResponseEntity.ok(logs);
	}


	//	@PermissionCheck(code = SystemPermissions.SYSTEM_ORG_MANAGE)
	@Parameter(name= "导入组织")
	@RequestMapping(value = "import",
			method = RequestMethod.POST)
	public ResponseEntity<Void> importOrganizations(
			@RequestParam("uploadFile") String uploadFile) {

		orgService.importOrganizations(uploadFile);
		return ResponseEntity.ok().build();
	}


	//	@PermissionCheck(code = SystemPermissions.SYSTEM_ORG_MANAGE)
//	@PermissionCheck(code = SystemPermissions.SYSTEM_ORG_MANAGE)
	@Parameter(name= "导出组织")
	@RequestMapping(value = "export",
			method = RequestMethod.GET)
	public ResponseEntity<Void> exportOrganizations(HttpServletRequest request, HttpServletResponse response) {
		String fileName = "组织列表";
		Page<OrgVo> orgs = orgService.findOrganizations(null, Pageable.unpaged());
		List<OrgVo> orglist = orgs.getContent();
		List<OrgVo> orgVos = DataUtils.toVo(orglist, OrgVo.class);

		LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

		headers.put("name", "组织名称");
		headers.put("hierarchyCode", "组织层次代码");

		List<HashMap> orgMapList=new ArrayList<>();
		orgVos.forEach(orgVo -> {
			HashMap<String, String> orgVoMap = new HashMap<>();
			orgVoMap.put("name",orgVo.getName());
			orgVoMap.put("hierarchyCode",orgVo.getHierarchyCode());
			orgMapList.add(orgVoMap);
		});

		try {
			ExcelUtil.processResponseHeader(request, response, "机构");
			ExcelUtil.exportExcel(headers, orgMapList, response.getOutputStream());

		} catch (Exception e) {
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Parameter(name= "导出组织模板")
	@RequestMapping(value = "template",
			method = RequestMethod.GET)
	public ResponseEntity<Void> importOrganizationTemplate(HttpServletRequest request, HttpServletResponse response) {
		String fileName = "组织列表";
		Page<OrgVo> orgs = orgService.findOrganizations(null, Pageable.unpaged());
		List<OrgVo> orglist = orgs.getContent();

		LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

		headers.put("name", "组织名称");
		headers.put("hierarchyCode", "组织层次代码");

		List<HashMap> orgMapList=new ArrayList<>();
		for (int i = 0; i < orglist.size(); i++) {
			if(i>=5)
				break;
			HashMap<String, String> orgVoMap = new HashMap<>();
			orgVoMap.put("name",orglist.get(i).getName());
			orgVoMap.put("hierarchyCode",orglist.get(i).getHierarchyCode());
			orgMapList.add(orgVoMap);
		}
		try {
			ExcelUtil.processResponseHeader(request, response, "机构导入模板");
			ExcelUtil.exportExcel(headers, orgMapList, response.getOutputStream());

		} catch (Exception e) {
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}


	//	@PermissionCheck(code = SystemPermissions.SYSTEM_ORG_MANAGE)
	@Parameter(name="上移组织")
	@RequestMapping(value = "moveUp/{upOrgId}",
			method = RequestMethod.POST)
	public ResponseEntity<Void> moveUpOrganization(@PathVariable(name = "upOrgId") Long upOrgId) {
		orgService.moveUpOrganization(upOrgId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Parameter(name= "下移组织")
	@RequestMapping(value = "moveDown/{downOrgId}",
			method = RequestMethod.POST)
	public ResponseEntity<Void> moveDownOrganization(@PathVariable(name = "downOrgId") Long downOrgId) {
		orgService.moveDownOrganization(downOrgId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	@Parameter(name= "查询部门名称是否重复")
	@RequestMapping(value = "/exist-name/{name}", method = RequestMethod.GET)
	public ResponseEntity isExistsByName(@PathVariable String name) {
		Boolean isExists = orgService.checkExistsByName(name);
		return ResponseEntity.ok(isExists);
	}


//	@PermissionCheck(code = SystemPermissions.SYSTEM_ORG_MANAGE)
//	public ResponseEntity<Void> orgOutset(@ApiParam(value = "单位Id", required = true) @PathVariable(required = true) Long companyId,
//										  @ApiParam(value = "外嵌入的子orgId", required = true) @PathVariable(required = true) Long childOrgId,
//										  @ApiParam(value = "被外嵌入的父orgId", required = true) @PathVariable(required = true) Long parentOrgId,
//										  @ApiParam(value = "") @RequestBody(required = false) List<Map> content) {
//
//		orgService.batchUpdateParentIdAndHcode(content);
//
//		return new ResponseEntity<Void>(HttpStatus.OK);
//	}
}
