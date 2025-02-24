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

import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.service.ProjectRoleMemberService;
import com.harbortek.helm.tracker.vo.ProjectRoleMemberVo;
import com.harbortek.helm.tracker.vo.users.IdentityByGroupVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Tag(name = "项目角色关联用户管理")
@RequestMapping(value = "/tracker/project/role/member")
public class ProjectRoleMemberApi {

    private final Logger logger = LoggerFactory.getLogger(ProjectRoleMemberApi.class);
    @Autowired
    ProjectRoleMemberService projectRoleMemberService;

    @Parameter(name="查询项目角色关联用户定义列表接口")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<List<ProjectRoleMemberVo>> findProjectRoleMembers(
            @RequestParam(value = "projectId", required = true) Long projectId) {
        List<ProjectRoleMemberVo> projectRoleMembers =
                projectRoleMemberService.findProjectRoleMembers(projectId,false);

        return ResponseEntity.ok(projectRoleMembers);
    }

    @Parameter(name="增加项目成员关系接口")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<Boolean> updateProjectRoleMember(@RequestParam Long projectId, @RequestParam Long roleId,
                                                     @RequestBody List<UserVo> users) {
        projectRoleMemberService.createProjectRoleMembers(projectId,roleId,users);
        return ResponseEntity.ok(true);
    }

    @Parameter(name="删除项目成员关系接口")
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    ResponseEntity<Boolean> deleteProjectRoleMembers(@RequestParam Long projectId, @RequestParam Long roleId,
            @RequestBody List<UserVo> users) {
        projectRoleMemberService.deleteProjectRoleMembers(projectId, roleId, users);
        return ResponseEntity.ok(true);
    }

    @Parameter(name="查询项目成员")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    ResponseEntity<List<UserVo>> findProjectUsers(
            @RequestParam(value = "projectId", required = true) Long projectId) {
        List<UserVo> users =
                projectRoleMemberService.findProjectUsers(projectId);

        return ResponseEntity.ok(users);
    }
    @Parameter(name="查询项目成员")
    @RequestMapping(value = "/user-by-ids", method = RequestMethod.GET)
    ResponseEntity<List<UserVo>> findProjectUserByIds(
            @RequestParam(value = "ids", required = true) List<Long> ids)  {
        List<UserVo> users =
                projectRoleMemberService.findProjectUsersByProjectIds(ids);
        return ResponseEntity.ok(users);
    }
    @Parameter(name="查询项目关联角色/特殊角色/项目成员")
    @RequestMapping(value = "/rolesAndMembers", method = RequestMethod.GET)
    ResponseEntity<IdentityByGroupVo> findProjectRolesAndMembers(
            @RequestParam(value = "projectId", required = true) Long projectId,
            @RequestParam(value = "scope", required = true) String scope) {

        IdentityByGroupVo vo = projectRoleMemberService.findProjectRolesAndMembers(projectId,scope);

        return ResponseEntity.ok(vo);
    }



}
