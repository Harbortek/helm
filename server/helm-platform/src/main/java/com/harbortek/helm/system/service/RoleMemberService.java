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

import com.harbortek.helm.system.vo.RoleMemberVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RoleMemberService {

    void createRoleMember(RoleMemberVo roleMemberVo);
    void createRoleMembers(List<RoleMemberVo> roleMembers);


    List<RoleMemberVo> findRoleMembers(String scope, Long ownerResourceId, List<Long> userIds,
                                           List<Long> roleIds);
    void deleteRoleMember(Long userId, Long roleId);

    void deleteRoleMembers(String scope, Long ownerResourceId, List<Long> userIds, List<Long> roleIds);

    Map<Long, Integer> countRoleMembersGroupByRoleId(String scope, Long ownerResourceId,  List<Long> userIds, List<Long> roleIds);

    List<RoleMemberVo> findSystemRoleMembers();

    List<RoleMemberVo> findRoleMembersByResourceId(Long ownerResourceId);

    List<RoleMemberVo> findRoleMembersByResourceIdAndUserId(String scope, Long ownerResourceId, Long userId);

    List<RoleMemberVo> findSystemRoleMembersByUserId(Long userId);
}
