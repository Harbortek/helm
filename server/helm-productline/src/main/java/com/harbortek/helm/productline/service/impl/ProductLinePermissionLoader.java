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

package com.harbortek.helm.productline.service.impl;

import com.harbortek.helm.common.config.module.PermissionLoader;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.productline.service.ProductLineService;
import com.harbortek.helm.productline.vo.ProductLineVo;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.service.RoleMemberService;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.vo.GrantedPermission;
import com.harbortek.helm.system.vo.RoleMemberVo;
import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.PermissionCacheUtils;
import com.harbortek.helm.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service
@Slf4j
public class ProductLinePermissionLoader implements PermissionLoader {

    @Autowired
    RoleService roleService;

    @Autowired
    RoleMemberService roleMemberService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    ProductLineService productLineService;

    @Override
    public List<GrantedPermission> loadPermissions(UserVo user) {
        Long userId = user.getId();
        Long productLineId = (Long)SecurityUtils.get(SecurityUtils.PRODUCT_LINE_ID);
        if (!ObjectUtils.isValid(productLineId)){
            return Collections.emptyList();
        }

        List<GrantedPermission> cachedPermissions = PermissionCacheUtils.getGrantedPermissions(productLineId, userId);
        if (ObjectUtils.isNotEmpty(cachedPermissions)){
            return cachedPermissions;
        }

        ProductLineVo productLine = productLineService.findOneProductLine(productLineId);

        List<RoleMemberVo> roleMembers =
                roleMemberService.findRoleMembersByResourceIdAndUserId(SpecialRole.SCOPE_PRODUCT_LINE, productLineId, userId);
        List<Long> roleIds = roleMembers.stream().map(RoleMemberVo::getRoleId).collect(Collectors.toList());
        List<RoleVo> roles = roleService.findRolesByIds(roleIds);

        List<GrantedPermission> permissions = new ArrayList<>();
        permissions.addAll(loadProductLinePermissions(userId, productLine, roles));

        PermissionCacheUtils.putGrantedPermissions(productLineId, userId, permissions);

        return permissions;

    }

    /**
     * 加载产品线权限
     * @param userId
     * @param productLine
     * @param roles
     * @return
     */
    private List<GrantedPermission> loadProductLinePermissions(Long userId, ProductLineVo productLine,
                                                                      List<RoleVo> roles) {

        List<GrantedPermission> permissions = new ArrayList<>();
        List<PermissionGrantVo> projectPermissions = productLineService.findPermissionGrants(productLine.getId());

        for (PermissionGrantVo permissionGrantVo : projectPermissions) {
            if (checkUserInIdentity(userId, permissionGrantVo.getGranted(), productLine, roles)) {
                permissions.add(
                        GrantedPermission.builder().name(permissionGrantVo.getPermissionName())
                                         .resourceId(productLine.getId())
                                         .build());
            }
        }
        return permissions;
    }

    /**
     * 判断用户是否在权限中
     * @param userId
     * @param granted
     * @param project
     * @param roles
     * @return
     */
    private boolean checkUserInIdentity(Long userId, List<BaseIdentity> granted, ProductLineVo project, List<RoleVo> roles) {
        for (BaseIdentity baseIdentity : granted) {
            if (baseIdentity.getType().equals(IdentityTypes.USER) && baseIdentity.getId().equals(userId)) {
                return true;
            } else if (baseIdentity.getType().equals(IdentityTypes.ROLE) && hasRole(roles, baseIdentity.getId())) {
                return true;
            } else if (baseIdentity.getType().equals(IdentityTypes.SPECIAL_ROLE) &&
                    hasSpecialRole(roles, baseIdentity.getId(), userId, project)) {
                return true;
            }

        }
        return false;
    }

    /**
     * 判断是否有角色
     * @param roles
     * @param roleId
     * @return
     */
    private boolean hasRole(List<RoleVo> roles, Long roleId) {
        for (RoleVo role : roles) {
            if (role.getId().equals(roleId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否有特殊角色
     * @param roles
     * @param identityId
     * @param userId
     * @param productLine
     * @return
     */
    private boolean hasSpecialRole(List<RoleVo> roles, Long identityId, Long userId, ProductLineVo productLine) {
        RoleVo role = roles.stream().filter(r -> r.getId().equals(identityId)).findFirst().orElse(null);
        if (role == null || !role.getSpecialRole()) {
            return false;
        }
        if (SpecialRole.ALL_USERS.equals(role.getSpecialRoleType())) {
            return true;
        } else if (SpecialRole.PRODUCT_LINE_OWNER.equals(role.getSpecialRoleType())) {
            return Objects.equals(productLine.getOwner().getId(), userId);
        }
        return false;
    }
}
