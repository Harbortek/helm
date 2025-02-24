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

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.tracker.service.ProjectPermissionService;
import com.harbortek.helm.tracker.service.ProjectRoleMemberService;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Tag(name = "项目权限管理")
@RequestMapping(value = "/tracker/project/permission")
public class ProjectPermissionApi {

    @Autowired
    ProjectPermissionService projectPermissionService;

    @Autowired
    ProjectRoleMemberService projectRoleMemberService;

//    @Parameter(name="查询项目权限授权列表")
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    ResponseEntity<List<ProjectPermissionEntity>> findPermissions(
//            @RequestParam(value = "projectId", required = true) Long projectId) {
//        List<ProjectPermissionEntity> permissions = projectPermissionService.findPermissions(projectId);
//        return ResponseEntity.ok(permissions);
//    }

    @Parameter(name="查询项目权限授权列表")
    @RequestMapping(value = "/list-grant", method = RequestMethod.GET)
    ResponseEntity<List<PermissionGrantVo>> findPermissionGrants(
            @RequestParam(value = "projectId", required = true) Long projectId) {

        List<PermissionGrantVo> permissionGrantList = projectPermissionService.findPermissionGrantList(projectId);

        return ResponseEntity.ok(permissionGrantList);
    }

    @Parameter(name="增加项目权限授权")
    @RequestMapping(value = "/grant", method = RequestMethod.POST)
    ResponseEntity<Void> grantPermission(@RequestParam(value = "projectId", required = true) Long projectId,
                                         @RequestParam(value = "permission", required = true) String permission,
                                         @RequestBody BaseIdentity identity) {


        projectPermissionService.grantPermission(projectId, permission, identity);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除项目权限授权")
    @RequestMapping(value = "/unGrant", method = RequestMethod.POST)
    ResponseEntity<Void> unGrantPermission(@RequestParam(value = "projectId", required = true) Long projectId,
                                           @RequestParam(value = "permission", required = true) String permission,
                                           @RequestBody BaseIdentity identity) {
        projectPermissionService.unGrantPermission(projectId, permission, identity);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询拥有权限res")
    @RequestMapping(value = "/projectIds/{permission}", method = RequestMethod.GET)
    ResponseEntity<List<Long>> findProjectIdsByPermission(@PathVariable String permission) {
        List<Long> projectIds = projectPermissionService
                .findProjectIdsByPermission(SecurityUtils.getCurrentUser().getId(), permission);
        return ResponseEntity.ok(projectIds);
    }


}
