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
import com.harbortek.helm.system.entity.ParamEntity;
import com.harbortek.helm.system.service.ParamService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Tag(name = "参数管理")
@RequestMapping(value = "/sys/param")
public class ParamApi {
	@Autowired
	ParamService paramService;

	@Parameter(name="查询参数列表接口")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<Page<ParamEntity>> listParams(String keyword, Pageable pageable) {
		Page<ParamEntity> params = paramService.findParams(keyword, pageable);
		return ResponseEntity.ok(params);
	}

	@Parameter(name="查询参数信息接口")
	@RequestMapping(value = "/{paramId}", method = RequestMethod.GET)
	public ResponseEntity getParam(@PathVariable Long paramId) {
		ParamEntity p = paramService.findOneParam(paramId);
		return ResponseEntity.ok(p);
	}

	@Parameter(name="新增参数信息接口")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_PARAM)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity addParam(ParamEntity param) {

		ParamEntity p = paramService.createParam(param);
		return ResponseEntity.ok(p);
	}

	@Parameter(name="更新参数信息接口")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_PARAM)
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public ResponseEntity updateParam(ParamEntity param) {
		ParamEntity p = paramService.updateParam(param);
		return ResponseEntity.ok(p);
	}

	@Parameter(name="删除参数信息接口")
	@RequestMapping(value = "/{paramId}", method = RequestMethod.DELETE)
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_PARAM)
	public ResponseEntity delParam(@PathVariable Long paramId) {
		paramService.deleteParamById(paramId);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="批量删除参数信息接口")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_PARAM)
	@RequestMapping(value = "/all", method = RequestMethod.DELETE)
	public ResponseEntity delParams(String paramIds) {
		List<Long> ids = new ArrayList<>();
		for (String idStr : Arrays.asList(paramIds.split(","))) {
			ids.add(Long.parseLong(idStr));
		}
		paramService.batchDelete(ids);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="查询参数名称是否重复")
	@RequestMapping(value = "/exist-name/{name}", method = RequestMethod.GET)
	public ResponseEntity isExistsByName(@PathVariable String name) {
		Boolean isExists = paramService.checkExistsByName(name);
		return ResponseEntity.ok(isExists);
	}

	@Parameter(name="查询参数编码是否重复")
	@RequestMapping(value = "/exist-code/{code}", method = RequestMethod.GET)
	public ResponseEntity isExistsByCode(@PathVariable String code) {
		Boolean isExists = paramService.checkExistsByCode(code);
		return ResponseEntity.ok(isExists);
	}
}
