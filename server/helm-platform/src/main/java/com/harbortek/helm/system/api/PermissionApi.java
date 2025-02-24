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

import com.harbortek.helm.common.config.module.ModuleManager;
import com.harbortek.helm.common.vo.PermissionDefinition;
import com.harbortek.helm.system.service.RoleService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Tag(name = "权限管理")
@RequestMapping(value = "/sys/perm")
public class PermissionApi {

	@Autowired
	private ModuleManager moduleManager;
	@Autowired
	private RoleService roleService;

	@Parameter(name="查询权限列表")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<Map<String, List<PermissionDefinition>>> getGlobalPermissionDefinitions() throws Exception {
		List<PermissionDefinition> perms = moduleManager.getGlobalPermissionDefinitions();
		Map<String, List<PermissionDefinition>> groupPerms =
				perms.stream().collect(
				Collectors.groupingBy(PermissionDefinition::getGroup));
		return ResponseEntity.ok(groupPerms);
	}

}
