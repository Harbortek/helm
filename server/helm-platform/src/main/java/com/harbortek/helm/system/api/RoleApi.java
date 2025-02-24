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
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.system.config.SystemModulePermissions;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.vo.PermissionVo;
import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.util.IDUtils;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "角色管理")
@RequestMapping(value = "/sys/role")
public class RoleApi {
	@Autowired
	RoleService roleService;

	@Autowired
	PermissionService permissionService;

	@Parameter(name="查询角色列表接口")
	@RequestMapping(value = "/list-all", method = RequestMethod.GET)
	public ResponseEntity<List<RoleVo>> listRolesNoPage(String scope,Long ownerResourceId) {
		List<RoleVo> roles = roleService.findRoles(scope,ownerResourceId);
		return ResponseEntity.ok(roles);
	}

	@Parameter(name="查询角色信息接口")
	@RequestMapping(value = "/{roleId}", method = RequestMethod.GET)
	public ResponseEntity<RoleVo> getRole(@PathVariable Long roleId) {
		RoleVo p = roleService.findOneRole(roleId);
		return ResponseEntity.ok(p);
	}

	@Parameter(name="新增角色信息接口")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_ROLE)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<RoleVo> addRole(RoleVo role) {
		RoleVo p = roleService.createRole(role);
		return ResponseEntity.ok(p);
	}

	@Parameter(name="更新角色信息接口")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_ROLE)
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public ResponseEntity<RoleVo> updateRole(RoleVo role) {
		RoleVo p = roleService.updateRole(role);
		return ResponseEntity.ok(p);
	}

	@Parameter(name="删除角色信息接口")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_ROLE)
	@RequestMapping(value = "/{roleId}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
		roleService.deleteRole(roleId);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="批量删除角色信息接口")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_ROLE)
	@RequestMapping(value = "/all", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteRoles(String roleIds) {
		List<Long> ids = new ArrayList<>();
		for (String idStr : roleIds.split(",")) {
			ids.add(Long.parseLong(idStr));
		}
		roleService.deleteRolesByIds(ids);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="查询角色名称是否重复")
	@RequestMapping(value = "/exist-name/{name}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> isExistsByName(@RequestParam String name, @RequestParam(required = false) String scope,
												  @RequestParam(required = false) Long ownerResourceId) {
		Boolean isExists = roleService.checkExistsByName(name,scope,ownerResourceId);
		return ResponseEntity.ok(isExists);
	}

	@Parameter(name="查询角色权限")
	@RequestMapping(value = "/perms/{roleId}", method = RequestMethod.GET)
	public ResponseEntity<List<String>> findRolePerms(@PathVariable("roleId") Long roleId) {
		List<PermissionVo> permissionVos = permissionService.findGlobalPermissionsByIdentityId(roleId);
		List<String> permissions = permissionVos.stream().map(PermissionVo::getName).collect(Collectors.toList());
		return ResponseEntity.ok(permissions);
	}

	@Parameter(name="更新角色权限")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_ROLE)
	@RequestMapping(value = "/perms/{roleId}", method = RequestMethod.POST)
	public ResponseEntity<Void> saveRolePerms(@PathVariable("roleId") Long roleId, @RequestBody List<String> perms) {
		permissionService.unGrantByIdentityId(roleId);
		List<PermissionVo> permissionVos = new ArrayList<>();
		BaseIdentity identity = new BaseIdentity();
		identity.setId(roleId);
		identity.setType(IdentityTypes.ROLE);
		perms.forEach(permission -> {
			PermissionVo permissionVo = new PermissionVo();
			permissionVo.setId(IDUtils.getId());
			permissionVo.setName(permission);
			permissionVo.setIdentity(identity);
			permissionVos.add(permissionVo);
		});

		permissionService.grant(permissionVos);
		return ResponseEntity.ok().build();
	}
}
