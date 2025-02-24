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

package com.harbortek.helm.system.service;

import com.harbortek.helm.system.vo.RoleVo;

import java.util.List;

public interface RoleService {

    RoleVo createRole(RoleVo role);

    RoleVo updateRole(RoleVo role);

    void deleteRole(Long roleId);

    RoleVo findOneRole(Long roleId);

    List<RoleVo> findRoles(String scope, Long ownerResourceId);

    Boolean checkExistsByName(String name, String scope, Long ownerResourceId);

    void deleteRolesByIds(List<Long> ids);

    void createRoles(List<RoleVo> roles);

    List<RoleVo> findRolesByResourceId(Long ownerResourceId);

    void deleteRolesByResourceId(Long ownerResourceId);

    List<RoleVo> findRolesByIds(List<Long> roleIds);

//    List<RoleVo> findSpecialRoles(String scope, String specialRoleType);
//
//    RoleVo findOneSpecialRole(String scope, Long resourceId, String specialRoleType);
}
