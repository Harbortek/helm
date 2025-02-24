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

import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.vo.RoleVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Tag(name = "项目角色管理")
@RequestMapping(value = "/tracker/project/{projectId}/role")
@Slf4j
public class ProjectRoleApi {

    @Autowired
    RoleService roleService;

    @Parameter(name="查询项目角色定义列表接口")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<List<RoleVo>> findProjectRolesByProject(@RequestParam(value = "projectId", required = true) Long projectId) {
        List<RoleVo> roles = roleService.findRoles(SpecialRole.SCOPE_PROJECT, projectId);
        return ResponseEntity.ok(roles);
    }

    @Parameter(name="查询项目角色定义接口")
    @RequestMapping(value = "/{roleId}", method = RequestMethod.GET)
    ResponseEntity<RoleVo> findProjectRole(@PathVariable(value = "projectId", required = true) Long projectId,
                                           @PathVariable(value = "roleId", required = true) Long roleId) {
        RoleVo vo = roleService.findOneRole(roleId);
        return ResponseEntity.ok(vo);
    }

    @Parameter(name="保存角色定义接口")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<RoleVo> updateProjectRole(@PathVariable(value = "projectId", required = true) Long projectId,@RequestBody RoleVo projectRole) {
        projectRole.setOwnerResourceId(projectId);
        RoleVo re = roleService.updateRole(projectRole);
        return ResponseEntity.ok(re);
    }

    @Parameter(name="创建一个角色")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<RoleVo> createProjectRole(@PathVariable(value = "projectId", required = true) Long projectId,@RequestBody RoleVo projectRole) {
        projectRole.setOwnerResourceId(projectId);
        RoleVo roleVo = roleService.createRole(projectRole);
        return ResponseEntity.ok(roleVo);
    }

    @Parameter(name="删除一个项目")
    @RequestMapping(value = "/{roleId}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteProjectRole(@PathVariable(value = "projectId", required = true) Long projectId,@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok().build();
    }


}
