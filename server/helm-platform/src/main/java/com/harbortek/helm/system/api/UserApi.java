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
import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.system.config.SystemModulePermissions;
import com.harbortek.helm.system.service.FileService;
import com.harbortek.helm.system.service.OrgService;
import com.harbortek.helm.system.service.UserService;
import com.harbortek.helm.system.vo.OrgVo;
import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.util.DateUtils;
import com.harbortek.helm.util.MD5Utils;
import com.harbortek.helm.util.MenuTreeUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Tag(name = "用户管理")
@RequestMapping(value = "/sys/user")
public class UserApi {
	@Autowired
	private UserService userService;
	@Autowired
	private OrgService orgService;
	@Autowired
	FileService fileService;

	@Parameter(name="查询用户列表接口")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<Page<UserVo>> listUsers(String keyword, Pageable pageable, Long orgId) {
		Page<OrgVo> data = orgService.findOrganizations("", Pageable.unpaged());
		List<Long> orgIds=null;
		if(orgId!=null){
			orgIds = MenuTreeUtil.ChildOrdId(data.getContent(), orgId);
		}

		Page<UserVo> users = userService.findUsers(keyword, pageable, orgIds);
		return ResponseEntity.ok(users);
	}

	@Parameter(name="查询用户信息接口")
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public ResponseEntity<UserVo> getUser(@PathVariable Long userId) {
		UserVo p = userService.findOneUser(userId);
		return ResponseEntity.ok(p);
	}

	@Parameter(name="新增用户信息接口")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_USER)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<UserVo> addUser(@RequestBody UserVo user,@RequestParam String password) {
		user.setPassword(password);
		UserVo p = userService.createUser(user);
		return ResponseEntity.ok(p);
	}

	@Parameter(name="更新用户信息接口")
//	@PermissionCheck(SystemPermissions.SYS_SETTINGS_USER)
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public ResponseEntity<UserVo> updateUser(@RequestBody UserVo user) {
		UserVo p = userService.updateUser(user);
		return ResponseEntity.ok(p);
	}

	@Parameter(name="删除用户信息接口")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_USER)
	@RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
		userService.deleteUser(userId);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="批量删除用户信息接口")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_USER)
	@RequestMapping(value = "/all", method = RequestMethod.DELETE)
	public ResponseEntity<Void> delUsers(String userIds) {
		List<Long> ids = new ArrayList<>();
		for (String idStr : Arrays.asList(userIds.split(","))) {
			ids.add(Long.parseLong(idStr));
		}
		userService.batchDelete(ids);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="查询用户名称是否重复")
	@RequestMapping(value = "/exist-name/{name}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> isExistsByName(@PathVariable String name) {
		Boolean isExists = userService.checkExistsByName(name);
		return ResponseEntity.ok(isExists);
	}

	@Parameter(name="重置密码")
	@RequestMapping(value = "/reset-pwd/{userId}", method = RequestMethod.POST)
	public ResponseEntity<Void> resetPwd(@PathVariable Long userId) {
		userService.resetPwd(userId);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="查询用户角色")
	@RequestMapping(value = "/roles/{userId}", method = RequestMethod.GET)
	public ResponseEntity<List<RoleVo>> getRoles(@PathVariable Long userId) {
		List<RoleVo> roles = userService.findGlobalRolesByUserId(userId);
		return ResponseEntity.ok(roles);
	}

	@Parameter(name="关联角色")
	@PermissionCheck(SystemModulePermissions.SYS_SETTINGS_USER)
	@RequestMapping(value = "/roles/{userId}", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateRoles(@PathVariable Long userId, @RequestBody List<Long> roles) {
		userService.updateRoles(userId, roles);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="更新密码")
	@RequestMapping(value = "/password", method = RequestMethod.PUT)
	public ResponseEntity<Void> updatePwd(@RequestParam Long id, @RequestParam String password,
			@RequestParam String oldPwd) {
		UserVo oldUser = userService.findOneUser(id);
		if (!oldUser.getPassword().equals(MD5Utils.string2MD5(oldPwd))) {
			throw new ServiceException("旧密码不正确!");
		}
		oldUser.setPassword(MD5Utils.string2MD5(password));
		userService.updateUser(oldUser);
		return ResponseEntity.ok().build();
	}

	@Parameter(name="更新头像")
	@RequestMapping(value = "/avatar", method = RequestMethod.POST)
	public ResponseEntity<String> updateAvatar(@RequestParam Long id, @RequestParam("file") MultipartFile file)
			throws Exception {
		UserVo oldUser = userService.findOneUser(id);
		String fileUrl = fileService.upload(file, DateUtils.getCurrDate());
		oldUser.setIcon(fileUrl);
		userService.updateUser(oldUser);
		return ResponseEntity.ok(fileUrl);
	}

}
