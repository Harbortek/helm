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

import com.harbortek.helm.common.config.module.ModuleManager;
import com.harbortek.helm.common.constants.Constants;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.common.vo.PermissionDefinition;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.service.*;
import com.harbortek.helm.system.vo.PermissionVo;
import com.harbortek.helm.system.vo.RoleMemberVo;
import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SystemDataInitServiceImpl implements SystemDataInitService {
    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    RoleMemberService roleMemberService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    ModuleManager moduleManager;

    @Override
    public void initializeSystemData(ApplicationContext applicationContext) {
        // 初始化用户
        UserVo adminUser = userService.findOneUser(1L);
        if (adminUser == null) {
            adminUser =
                    UserVo.builder().id(1L).name("胡隽").loginName("admin").password("admin").status(Constants.ENABLE).build();
            userService.createUser(adminUser);
        }

        // 初始化角色
        RoleVo adminRole = roleService.findOneRole(2L);
        if (adminRole == null) {
            adminRole = RoleVo.builder().id(2L).name("管理员").specialRole(false).scope(SpecialRole.SCOPE_GLOBAL).build();
            roleService.createRole(adminRole);


            List<PermissionDefinition> perms = moduleManager.getGlobalPermissionDefinitions();
            List<PermissionVo> permissions = new ArrayList<>();
            BaseIdentity identity = new BaseIdentity();
            identity.setType(IdentityTypes.ROLE);
            identity.setReferTo(new IdNameReference<>(adminRole));
            for (PermissionDefinition permissionDefinition : perms) {
                PermissionVo permission =
                        PermissionVo.builder().id(IDUtils.getId()).name(permissionDefinition.getName()).identity(identity).build();
                permissions.add(permission);
            }
            permissionService.grant(permissions);

        }

        List<RoleMemberVo> roleMembers = roleMemberService.findSystemRoleMembers();

        if (roleMembers == null || roleMembers.isEmpty()) {
            RoleMemberVo roleMember =
                    RoleMemberVo.builder().id(IDUtils.getId()).roleId(adminRole.getId()).userId(adminUser.getId())
                                .build();
            roleMemberService.createRoleMember(roleMember);
        }
    }
}
