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

package com.harbortek.helm.tracker.service;

import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.vo.ProjectRoleMemberVo;
import com.harbortek.helm.tracker.vo.users.IdentityByGroupVo;

import java.util.List;

public interface ProjectRoleMemberService {

	void createProjectRoleMembers(Long projectId, Long roleId, List<UserVo> users);

	void createProjectRoleMembers(Long projectId, List<ProjectRoleMemberVo> roleMemberVos);

	void deleteProjectRoleMembers(Long projectId, Long roleId, List<UserVo> memberEntities);

	List<ProjectRoleMemberVo> findProjectRoleMembers(Long projectId,boolean includeSpecialRole);

	IdentityByGroupVo findProjectRolesAndMembers(Long projectId, String scope);

	List<UserVo> findProjectUsers(Long projectId);

	List<UserVo> findProjectUsers(Long projectId,List<Long> roleIds);

	List<UserVo> findProjectUsersByProjectIds(List<Long> ids);

    List<Long> findProjectIdsByUserId(Long userId);

	boolean existsUserRoles(Long projectId, List<Long> roleIds, Long userId);

    void createDefaultRoles(Long projectId,Long userId);
}
