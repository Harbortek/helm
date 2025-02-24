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

package com.harbortek.helm.tracker.service.impl;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.service.RoleMemberService;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.vo.PermissionVo;
import com.harbortek.helm.tracker.constants.ProjectPermissions;
import com.harbortek.helm.tracker.dao.ProjectDao;
import com.harbortek.helm.tracker.dao.ProjectPermissionDao;
import com.harbortek.helm.tracker.service.ProjectPermissionService;
import com.harbortek.helm.tracker.service.ProjectRoleMemberService;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.tracker.vo.users.IdentityByGroupVo;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.PermissionCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProjectPermissionServiceImpl implements ProjectPermissionService {

    @Autowired
    RoleService roleService;

    @Autowired
    RoleMemberService roleMemberService;

    @Autowired
    ProjectRoleMemberService projectRoleMemberService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    ProjectDao projectDao;

    @Autowired
    ProjectPermissionDao projectPermissionDao;


    @Override
    public List<PermissionGrantVo> findPermissionGrantList(Long projectId) {
        IdentityByGroupVo groupVo = projectRoleMemberService.findProjectRolesAndMembers(projectId,
                                                                                        SpecialRole.SCOPE_PROJECT);

        List<PermissionVo> permissionVos = permissionService.findPermissions(null, null, null, List.of(projectId));

        Map<String, List<BaseIdentity>> map = new HashMap<>();
        permissionVos.forEach(permissionVo -> {
            String key = permissionVo.getName();
            List<BaseIdentity> list = map.computeIfAbsent(key, k -> new ArrayList<>());
            list.add(permissionVo.getIdentity());
        });

        List<String> permissions = ProjectPermissions.ALL_PERMISSIONS;
        List<PermissionGrantVo> grantVoList = new ArrayList<>();
        permissions.forEach(permission -> {
            PermissionGrantVo vo = new PermissionGrantVo();
            vo.setPermissionName(permission);
            List<BaseIdentity> identityList = map.getOrDefault(permission, new ArrayList<>());
            identityList.forEach(identity -> {
                if (identity != null) {
                    BaseIdentity newIdentity = ObjectUtils.findById(groupVo, identity.getId(), BaseIdentity.class);
                    if (newIdentity != null) {
                        identity.setId(newIdentity.getId());
                        identity.setName(newIdentity.getName());
                        identity.setDescription(newIdentity.getDescription());
                        identity.setIcon(newIdentity.getIcon());
                    }
                }
            });
            vo.setGranted(identityList);
            grantVoList.add(vo);
        });

        return grantVoList;
    }


    @Override
    public void grantPermission(Long projectId, String permission, BaseIdentity identity) {
        permissionService.grant(permission, identity, projectId);
        PermissionCacheUtils.evictPermissions(projectId);//清除ProjectPerm缓存

    }

    @Override
    public void unGrantPermission(Long projectId, String permission, BaseIdentity identity) {
        permissionService.unGrant(permission, identity, projectId);
        PermissionCacheUtils.evictPermissions(projectId);//清除ProjectPerm缓存
    }


    public List<Long> findProjectIdsByPermission(Long userId, String permissionName) {
        return projectPermissionDao.findProjectIdsByUserId(userId,permissionName);
//        List<Long> resourceIds =
//                permissionService.findResourceIdsByPermissionName(List.of(permissionName));
//        if (CollUtil.isEmpty(resourceIds)){
//            return Lists.newArrayList();
//        }
//        Map<Long, List<PermissionVo>> permissionMap =
//                permissionsByName.stream().collect(Collectors.groupingBy(PermissionVo::getResourceId));
//
//        List<Long> projectIds = new ArrayList<>();
//        permissionMap.keySet().forEach(projectId -> {
//            List<PermissionVo> permissionVos = permissionMap.get(projectId);
//            List<BaseIdentity> identities = permissionVos.stream().map(PermissionVo::getIdentity).collect(
//                    Collectors.toList());
//            if (checkUserInIdentity(userId, projectId, identities)) {
//                projectIds.add(projectId);
//            }
//        });
//        return projectIds;


//        //1.根据USERID直接查询
//        List<PermissionVo> permissionsByUser = permissionService.findPermissions(List.of(permissionName),
//                                                                                List.of(userId),
//                                                                             IdentityTypes.USER,
//                                                                             null);
//        List<Long> projectIdsByUser =
//                permissionsByUser.stream().map(PermissionVo::getResourceId).distinct().collect(Collectors.toList());
//
//
//        //2.根据用户查询角色,然后查询相关的项目ID
//        List<RoleMemberVo> roleMembers =
//                roleMemberService.findRoleMembers(SpecialRole.SCOPE_PROJECT,null,List.of(userId),null);
//
//        List<Long> roleIds = ObjectUtils.ids(roleMembers, RoleMemberVo.Fields.roleId);
//
//        List<PermissionVo> permissionsByCommonRole = permissionService.findPermissions(List.of(permissionName), roleIds,
//                                                                             IdentityTypes.ROLE,
//                                                                             null);
//        List<Long> projectIdsByCommonRole =
//                permissionsByCommonRole.stream().map(PermissionVo::getResourceId).distinct().collect(Collectors.toList());
//
//        //项目角色
//        //特殊角色-所有人
//        List<RoleVo> allUserRoles = roleService.findSpecialRoles(SpecialRole.SCOPE_PROJECT,SpecialRole.ALL_USERS);
//        List<Long> projectIdsByAllUser =
//                allUserRoles.stream().map(RoleVo::getOwnerResourceId).distinct().collect(Collectors.toList());
//
//        //特殊角色-项目负责人
//        List<RoleVo> projectOwnerRoles = roleService.findSpecialRoles(SpecialRole.SCOPE_PROJECT,SpecialRole.PROJECT_OWNER);
//        List<ProjectEntity> projects = projectDao.findProjectsByOwner(userId,null);
//        List<PermissionVo> permissionsByProjectOwner = permissionService.findPermissions(List.of(permissionName),
//                                                                                         ObjectUtils.ids(projectOwnerRoles),
//                                                                             IdentityTypes.SPECIAL_ROLE,
//                                                                             ObjectUtils.ids(projects));
//        List<Long> projectIdsByProjectOwner =
//                permissionsByProjectOwner.stream().map(PermissionVo::getResourceId).distinct().collect(Collectors.toList());
//
//
//
//        //特殊角色-所有项目成员
//        List<RoleVo> allMemberRoles = roleService.findSpecialRoles(SpecialRole.SCOPE_PROJECT,SpecialRole.PROJECT_ALL_MEMBERS);
//        List<Long> projectIdsByAllMember1 =
//                allMemberRoles.stream().map(RoleVo::getOwnerResourceId).distinct().collect(Collectors.toList());
//        List<Long> projectIdsByAllMember2 =
//                roleMembers.stream().map(RoleMemberVo::getOwnerResourceId).collect(Collectors.toList());
//        List<Long> projectIdsByAllMember = Lists.newArrayList(CollUtil.union(projectIdsByAllMember1,
//                                                                             projectIdsByAllMember2));
//
//        List<Long> projectIds = CollUtil.unionAll(projectIdsByUser,projectIdsByCommonRole,projectIdsByAllUser,
//                                                  projectIdsByProjectOwner,projectIdsByAllMember);
//
//        List<Long> projectIdList = projectDao
//                .findRecentProjects(null, projectIds, null)
//                .stream().map(BaseEntity::getId).collect(Collectors.toList());
//
//        return projectIdList;
    }

//    private boolean checkUserInIdentity(Long userId, Long projectId, List<BaseIdentity> granted) {
//        ProjectEntity project = projectDao.findOneProject(projectId);
//        List<RoleMemberVo> roleMembers = roleMemberService.findRoleMembersByResourceIdAndUserId(SpecialRole.SCOPE_PROJECT, projectId, userId);
//        List<RoleVo> roles = roleService.findRolesByIds(roleMembers.stream().map(RoleMemberVo::getRoleId).collect(Collectors.toList()));
//
//        List<Long> roleIds = ObjectUtils.ids(roles);
//        for (BaseIdentity baseIdentity : granted) {
//            if (baseIdentity.getType().equals(IdentityTypes.USER) && baseIdentity.getId().equals(userId)) {
//                return true;
//            } else if (baseIdentity.getType().equals(IdentityTypes.ROLE) && roleIds.contains(baseIdentity.getId())) {
//                return true;
//            } else if (baseIdentity.getType().equals(IdentityTypes.SPECIAL_ROLE) &&
//                    hasSpecialRole(roles, baseIdentity.getId(), userId, project)) {
//                return true;
//            }
//
//        }
//        return false;
//    }
//
//    private boolean hasSpecialRole(List<RoleVo> roles, Long identityId, Long userId, ProjectEntity project) {
//        Optional<RoleVo> role = roles.stream().filter(roleVo -> roleVo.getId().equals(identityId)).findFirst();
//        if (role.isPresent()) {
//
//            if (!role.get().getSpecialRole()) {
//                return false;
//            }
//            String specialRoleType = role.get().getSpecialRoleType();
//
//            if (SpecialRole.ALL_USERS.equals(specialRoleType)) {
//                return true;
//            } else if (SpecialRole.PROJECT_OWNER.equals(specialRoleType)) {
//                return Objects.equals(project.getOwnerId(), userId);
//            } else if (SpecialRole.PROJECT_ALL_MEMBERS.equals(specialRoleType)) {
//                return ObjectUtils.isNotEmpty(roles);
//            }
//        }
//        return false;
//    }
}
