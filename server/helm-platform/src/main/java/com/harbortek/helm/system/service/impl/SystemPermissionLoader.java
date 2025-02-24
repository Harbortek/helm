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

package com.harbortek.helm.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.harbortek.helm.common.config.module.PermissionLoader;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.service.RoleMemberService;
import com.harbortek.helm.system.vo.GrantedPermission;
import com.harbortek.helm.system.vo.PermissionVo;
import com.harbortek.helm.system.vo.RoleMemberVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.PermissionCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SystemPermissionLoader implements PermissionLoader {
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleMemberService roleMemberService;

    @Override
    public List<GrantedPermission> loadPermissions(UserVo user) {
        Long userId = user.getId();
        List<GrantedPermission> cachedPermissions = PermissionCacheUtils.getGrantedPermissions(null, userId);
        if (ObjectUtils.isNotEmpty(cachedPermissions)) {
            return cachedPermissions;
        }


        List<Long> roleIds =
                roleMemberService.findRoleMembersByResourceIdAndUserId(SpecialRole.SCOPE_GLOBAL, null, userId).stream()
                                 .map(RoleMemberVo::getRoleId)
                                 .collect(Collectors.toList());

        List<PermissionVo> permissionsFromRole =
                permissionService.findGlobalPermissionsByIdentities(roleIds, IdentityTypes.ROLE);

        List<PermissionVo> permissionFromUser =
                permissionService.findGlobalPermissionsByIdentities(List.of(userId), IdentityTypes.USER);


        List<PermissionVo> all = CollUtil.unionAll(permissionsFromRole, permissionFromUser);
        List<GrantedPermission> grantedPermissions = all.stream().map(permissionEntity -> {
            return GrantedPermission.builder().name(permissionEntity.getName())
                                    .resourceId(permissionEntity.getResourceId()).build();
        }).collect(Collectors.toList());

        PermissionCacheUtils.putGrantedPermissions(null, userId, grantedPermissions);

        return grantedPermissions;
    }
}
