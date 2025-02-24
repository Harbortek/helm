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
import com.harbortek.helm.system.service.FileService;
import com.harbortek.helm.system.service.SystemService;
import com.harbortek.helm.system.vo.SystemVo;
import com.harbortek.helm.util.PathUtil;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "系统管理")
@RequestMapping(value = "/sys/system")
public class SystemApi {
	@Autowired
	SystemService systemService;
	@Autowired
	FileService fileService;

	@Parameter(name="分页查询系统列表接口")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<Page<SystemVo>> listSystems(String keyword, Pageable pageable) {
		Page<SystemVo> systems = systemService.findSystems(pageable);
		return ResponseEntity.ok(systems);
	}

	@Parameter(name="查询系统列表接口")
	@RequestMapping(value = "/list-all", method = RequestMethod.GET)
	public ResponseEntity<List<SystemVo>> listSystemsNoPage(String keyword) {
		List<SystemVo> systems = systemService.findAllSystems(keyword);
		return ResponseEntity.ok(systems);
	}

	@Parameter(name="查询系统信息接口")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<SystemVo> getSystem(@PathVariable Long id) {
		SystemVo p = systemService.findOneSystem(id);
		return ResponseEntity.ok(p);
	}

	@Parameter(name="新增系统信息接口")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_SYSTEM)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<SystemVo> addSystem(SystemVo system) {
		SystemVo p = systemService.createSystem(system);
		return ResponseEntity.ok(p);
	}

	@Parameter(name="更新系统信息接口")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_SYSTEM)
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public ResponseEntity<SystemVo> updateSystem(SystemVo system,MultipartFile file,MultipartFile loginFile) throws Exception {
		if(file != null){
			String fileUrl = fileService.upload(file, PathUtil.defaultStorePath());
			system.setLogo(fileUrl);
		}
		if(loginFile != null){
			String fileUrl = fileService.upload(loginFile, PathUtil.defaultStorePath());
			system.setLoginLogo(fileUrl);
		}
		SystemVo p = systemService.updateSystem(system);
		return ResponseEntity.ok(p);
	}

	@Parameter(name="删除系统信息接口")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_SYSTEM)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<SystemVo> deleteSystem(@PathVariable Long id) {
		systemService.deleteSystem(id);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="查询系统名称是否重复")
	@RequestMapping(value = "/exist-name/{name}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> isExistsByName(@PathVariable String name) {
		Boolean isExists = systemService.checkExistsByName(name);
		return ResponseEntity.ok(isExists);
	}

}
