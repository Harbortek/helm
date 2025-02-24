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

import com.harbortek.helm.common.config.module.PermissionLoader;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.service.RoleMemberService;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.vo.*;
import com.harbortek.helm.tracker.constants.TrackerPermissions;
import com.harbortek.helm.tracker.dao.ProjectDao;
import com.harbortek.helm.tracker.dao.TrackerDao;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.service.ProjectPageService;
import com.harbortek.helm.tracker.service.ProjectPermissionService;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.PermissionCacheUtils;
import com.harbortek.helm.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectPermissionLoader implements PermissionLoader {
    @Autowired
    private PermissionService permissionService;

    @Autowired
    RoleService roleService;

    @Autowired
    private RoleMemberService roleMemberService;


    @Autowired
    ProjectDao projectDao;

    @Autowired
    TrackerDao trackerDao;

    @Autowired
    ProjectPermissionService projectPermissionService;

    @Autowired
    ProjectPageService projectPageService;

    @Override
    public List<GrantedPermission> loadPermissions(UserVo user) {
        Long userId = user.getId();
        Long projectId = (Long) SecurityUtils.get(SecurityUtils.PROJECT_ID);

        if (!ObjectUtils.isValid(projectId)) {
            return Collections.emptyList();
        }

        List<GrantedPermission> cachedPermissions = PermissionCacheUtils.getGrantedPermissions(projectId, userId);
        if (ObjectUtils.isNotEmpty(cachedPermissions)) {
            return cachedPermissions;
        }


        ProjectEntity project = projectDao.findOneProject(projectId);
        if (project == null) {
            return Collections.emptyList();
        }
        List<RoleMemberVo> roleMembers =
                roleMemberService.findRoleMembersByResourceIdAndUserId(SpecialRole.SCOPE_PROJECT, projectId, userId);
        List<Long> roleIds = roleMembers.stream().map(RoleMemberVo::getRoleId).toList();
        List<RoleVo> roles =
                roleService.findRolesByResourceId(project.getId()).stream()
                        .filter(r -> r.getSpecialRole() || roleIds.contains(r.getId())).toList();

//        List<RoleMemberVo> roleMembers =
//                roleMemberService.findRoleMembersByResourceIdAndUserId(SpecialRole.SCOPE_PROJECT, projectId, userId);
//        List<Long> roleIds = roleMembers.stream().map(RoleMemberVo::getRoleId).collect(Collectors.toList());
//        List<RoleVo> roles = roleService.findRolesByIds(roleIds);

        List<GrantedPermission> permissions = new ArrayList<>();
        permissions.addAll(loadProjectPermissions(userId, project, roles));

        permissions.addAll(loadPagePermissions(userId, project, roles));

        permissions.addAll(loadTrackerPermissions(userId, project, roles));

        PermissionCacheUtils.putGrantedPermissions(projectId, userId, permissions);

        return permissions;
    }

    private List<GrantedPermission> loadTrackerPermissions(Long userId, ProjectEntity project,
                                                           List<RoleVo> roles) {
        List<GrantedPermission> permissions = new ArrayList<>();

        List<TrackerEntity> trackers = trackerDao.findByProject(project.getId());

        List<PermissionVo> permissionVos =
                permissionService.findPermissions(TrackerPermissions.TRACKER_PERMISSIONS, null, null,
                                                  ObjectUtils.ids(trackers));


        Map<Long, List<PermissionVo>> trackerPermissionMap =
                permissionVos.stream().collect(Collectors.groupingBy(PermissionVo::getResourceId));

        for (Long trackerId : trackerPermissionMap.keySet()) {
            Map<String, List<PermissionVo>> permissionMap =
                    trackerPermissionMap.get(trackerId).stream().collect(Collectors.groupingBy(PermissionVo::getName));
            List<PermissionGrantVo> trackerPermissions = new ArrayList<>();
            permissionMap.keySet().forEach(permissionName -> {
                List<PermissionVo> permissionVoList = permissionMap.get(permissionName);
                PermissionGrantVo trackerRolePermission = PermissionGrantVo.builder()
                                                                           .permissionName(permissionName)
                                                                           .granted(permissionVoList.stream()
                                                                                                    .map(PermissionVo::getIdentity)
                                                                                                    .collect(
                                                                                                            Collectors.toList()))
                                                                           .build();
                trackerPermissions.add(trackerRolePermission);
            });
            for (PermissionGrantVo permissionGrantVo : trackerPermissions) {
                if (checkUserInIdentity(userId, permissionGrantVo.getGranted(), project, roles)) {
                    permissions.add(
                            GrantedPermission.builder().name(permissionGrantVo.getPermissionName())
                                             .resourceId(trackerId)
                                             .build());
                }
            }
        }

        return permissions;

    }


    private List<GrantedPermission> loadPagePermissions(Long userId, ProjectEntity project,
                                                        List<RoleVo> roles) {
        List<Long> pageIds = projectPageService.findPageIdsByProjectId(project.getId());
        List<GrantedPermission> permissions = new ArrayList<>();
        List<PermissionVo> permissionVos = permissionService.findPermissions(null, ObjectUtils.ids(roles),
                                                                             IdentityTypes.ROLE,
                                                                             pageIds);
        permissionVos.forEach(permissionVo -> {
            List<BaseIdentity> baseIdentities = List.of(permissionVo.getIdentity());
            if (checkUserInIdentity(userId, baseIdentities, project, roles)) {
                permissions.add(
                        GrantedPermission.builder().name(permissionVo.getName())
                                         .resourceId(permissionVo.getResourceId())
                                         .build());
            }
        });
        return permissions;
    }

    private Collection<? extends GrantedPermission> loadProjectPermissions(Long userId, ProjectEntity project,
                                                                           List<RoleVo> roles) {
        List<GrantedPermission> permissions = new ArrayList<>();
        List<PermissionGrantVo> projectPermissions = projectPermissionService.findPermissionGrantList(project.getId());

        for (PermissionGrantVo permissionGrantVo : projectPermissions) {
            if (checkUserInIdentity(userId, permissionGrantVo.getGranted(), project, roles)) {
                permissions.add(
                        GrantedPermission.builder().name(permissionGrantVo.getPermissionName())
                                         .resourceId(project.getId())
                                         .build());
            }
        }
        return permissions;
    }

    private boolean checkUserInIdentity(Long userId, List<BaseIdentity> granted, ProjectEntity project,
                                        List<RoleVo> roles) {
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

    private boolean hasRole(List<RoleVo> roles, Long roleId) {
        for (RoleVo role : roles) {
            if (role.getId().equals(roleId)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSpecialRole(List<RoleVo> roles, Long identityId, Long userId, ProjectEntity project) {
        RoleVo role = roles.stream().filter(r -> r.getId().equals(identityId)).findFirst().orElse(null);
        if (role == null || !role.getSpecialRole()) {
            return false;
        }
        if (SpecialRole.ALL_USERS.equals(role.getSpecialRoleType())) {
            return true;
        } else if (SpecialRole.PROJECT_OWNER.equals(role.getSpecialRoleType())) {
            return Objects.equals(project.getOwnerId(), userId);
        } else if (SpecialRole.PROJECT_ALL_MEMBERS.equals(role.getSpecialRoleType())) {
            return ObjectUtils.isNotEmpty(roles);
        }
        return false;
    }


}
