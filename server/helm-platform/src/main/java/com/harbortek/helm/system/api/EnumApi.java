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
import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.vo.EnumCategoryVo;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.ScriptExecuteResult;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "字典管理")
@RequestMapping(value = "/sys/enum")
public class EnumApi {
	@Autowired
	EnumService enumService;


	@Parameter(name="查询枚举值分类列表")
	@RequestMapping(value = "/category/list", method = RequestMethod.GET)
	public ResponseEntity<Page<EnumCategoryVo>> findEnumCategories(@RequestParam(required = false)  String keyword,
																   @RequestParam(required = false)  Long projectId,
																   Pageable pageable) {
		Page<EnumCategoryVo> categories = enumService.findEnumCategories(keyword,projectId,pageable);
		return ResponseEntity.ok(categories);
	}

	@Parameter(name="创建枚举值分类")
	@RequestMapping(value = "/category", method = RequestMethod.POST)
	public ResponseEntity<Void> createEnumCategory(@RequestBody EnumCategoryVo categoryVo) {
		enumService.createEnumCategory(categoryVo);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="更新枚举值分类")
	@RequestMapping(value = "/category", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateEnumCategory(@RequestBody EnumCategoryVo categoryVo) {
		enumService.updateEnumCategory(categoryVo);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="删除枚举值分类")
	@RequestMapping(value = "/category/{categoryId}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteEnumCategory(@PathVariable Long categoryId) {
		enumService.deleteEnumCategory(categoryId);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="查询重复枚举值分类名称")
	@RequestMapping(value = "/category/existsName", method = RequestMethod.GET)
	public ResponseEntity<Boolean> checkDuplicateName(@RequestParam(required = false) Long projectId, @RequestParam String name) {
		boolean result = enumService.existsCategoryName(projectId, name);
		return ResponseEntity.ok(result);
	}


	@Parameter(name="查询枚举值列表")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<List<EnumItemVo>> findEnums(@RequestParam  Long categoryId, @RequestParam(required = false)  Long projectId) {
		List<EnumItemVo> enums = enumService.findEnumItems(categoryId, projectId);
		return ResponseEntity.ok(enums);
	}

	@Parameter(name="查询枚举值列表(CODE)")
	@RequestMapping(value = "/listByCode", method = RequestMethod.GET)
	public ResponseEntity<List<EnumItemVo>> findEnumsByCode(@RequestParam  String categoryCode,
													   @RequestParam(required = false)  Long projectId) {
		List<EnumItemVo> enums = enumService.findEnumItemsByCode(projectId,categoryCode);
		return ResponseEntity.ok(enums);
	}

	@Parameter(name="保存枚举值列表")
	@PermissionCheck(value = SystemModulePermissions.SYS_SETTINGS_ENUM)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<Void> updateEnums(@RequestParam  Long categoryId, @RequestParam(required = false)  Long projectId,
			@RequestBody List<EnumItemVo> enums) {
		enumService.updateEnumItems(categoryId, projectId, enums);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="获取枚举值")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<EnumItemVo> findOneEnum(@RequestParam Long itemId) {
		EnumItemVo itemVo = enumService.findOneEnumItemById(itemId);
		return ResponseEntity.ok(itemVo);
	}

	@Parameter(name="测试运行枚举脚本")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_ENUM)
	@RequestMapping(value = "testScript", method = RequestMethod.POST)
	public ResponseEntity<ScriptExecuteResult> testScript(@RequestBody EnumCategoryVo categoryVo) {
		ScriptExecuteResult result = enumService.testScript(categoryVo.getId(), categoryVo.getProjectId(),
													  categoryVo.getScript());
        return ResponseEntity.ok(result);
    }

}
