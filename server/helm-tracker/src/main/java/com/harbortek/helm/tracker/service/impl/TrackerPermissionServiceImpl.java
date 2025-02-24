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
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.service.RoleMemberService;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.vo.*;
import com.harbortek.helm.tracker.constants.TrackerPermissions;
import com.harbortek.helm.tracker.dao.ProjectDao;
import com.harbortek.helm.tracker.dao.TrackerItemDao;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.service.TrackerPermissionService;
import com.harbortek.helm.tracker.service.TrackerService;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.permissions.FieldPermission;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TrackerPermissionServiceImpl implements TrackerPermissionService {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    RoleService roleService;

    @Autowired
    private RoleMemberService roleMemberService;


    @Autowired
    ProjectDao projectDao;

    @Autowired
    TrackerService trackerService;

    @Autowired
    TrackerItemDao trackerItemDao;

    @Override
    public List<GrantedPermission> loadPermissions(Long trackerId, Long trackerItemId) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        Long projectId = (Long)SecurityUtils.get(SecurityUtils.PROJECT_ID);

        if (!ObjectUtils.isValid(projectId) || ObjectUtils.isValid(trackerId) || ObjectUtils.isValid(trackerItemId)) {
            return Collections.emptyList();
        }

        TrackerVo tracker = trackerService.findOneTracker(trackerId);

        TrackerItemVo trackerItem = DataUtils.toVo(trackerItemDao.findOneById(trackerItemId),TrackerItemVo.class);

        List<RoleMemberVo> roleMembers =
                roleMemberService.findRoleMembersByResourceIdAndUserId(SpecialRole.SCOPE_PROJECT, projectId, userId);
        List<Long> roleIds = roleMembers.stream().map(RoleMemberVo::getRoleId).collect(Collectors.toList());
        List<RoleVo> roles = roleService.findRolesByIds(roleIds);

        List<GrantedPermission> permissions = new ArrayList<>();
        permissions.addAll(loadTrackerPermissions(userId, tracker, trackerItem, roles));
        permissions.addAll(loadTrackerFieldPermissions(userId, tracker, trackerItem, roles));

        return permissions;
    }

    @Override
    public boolean hasTrackerPermission(List<String> permission, Long trackerId, Long trackerItemId) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        Long projectId = (Long)SecurityUtils.get(SecurityUtils.PROJECT_ID);

        TrackerVo tracker = trackerService.findOneTracker(trackerId);
        TrackerItemVo trackerItem = null;
        if(ObjectUtils.isNotEmpty(trackerItemId)){
            trackerItem = DataUtils.toVo(trackerItemDao.findOneById(trackerItemId),TrackerItemVo.class);
        }

        List<RoleMemberVo> roleMembers =
                roleMemberService.findRoleMembersByResourceIdAndUserId(SpecialRole.SCOPE_PROJECT, projectId, userId);
        List<Long> roleIds = roleMembers.stream().map(RoleMemberVo::getRoleId).collect(Collectors.toList());
        List<RoleVo> roles = roleService.findRolesByIds(roleIds);
        List<PermissionVo> permissions = permissionService.findPermissions(permission, null, null, List.of(trackerId));

        List<BaseIdentity> identityList = permissions.stream().map(PermissionVo::getIdentity).collect(Collectors.toList());
        return checkUserInIdentity(userId, identityList, tracker, trackerItem, roles);
    }

    @Override
    public List<Long> findTrackerIdsByPermission(Long userId, String permission,Long projectId,List<Long> trackerIds) {
        List<PermissionVo> permissions = permissionService.findPermissions(List.of(TrackerPermissions.ITEM_VIEW), null, null, trackerIds);
        Map<Long, List<PermissionVo>> permissionMap = permissions.stream().collect(Collectors.groupingBy(PermissionVo::getResourceId));
        List<Long> trackerIdsPerm=new ArrayList<>();

        List<Long> roleIds = roleMemberService.findRoleMembersByResourceIdAndUserId(SpecialRole.SCOPE_PROJECT, projectId,userId)
                .stream().map(RoleMemberVo::getRoleId).collect(Collectors.toList());
        List<RoleVo> roles = roleService.findRolesByIds(roleIds);
        TrackerVo tracker = TrackerVo.builder().projectId(projectId).build();
        permissionMap.keySet().forEach(trackerId -> {
            List<PermissionVo> permissionVos = permissionMap.get(trackerId);
            List<BaseIdentity> identities = permissionVos.stream().map(PermissionVo::getIdentity).collect(
                    Collectors.toList());
            if (checkUserInIdentity(userId, identities, tracker, null, roles)) {
                trackerIdsPerm.add(trackerId);
            }
        });

        return trackerIdsPerm;
    }

    @Override
    public List<TrackerSpecialRole> findTrackerSpecialRoles(List<String> permissions, List<Long> trackerIds) {
        List<PermissionVo> specialPerm = permissionService.findPermissions(permissions, null, IdentityTypes.SPECIAL_ROLE, trackerIds);

        List<Long> specialRoleIds = specialPerm.stream().map(p->p.getIdentity().getId()).collect(Collectors.toList());
        List<RoleVo> rolesByIds = roleService.findRolesByIds(specialRoleIds);
        List<TrackerSpecialRole> specialRoles= new ArrayList<>();
        specialPerm.forEach(perm->{
            RoleVo roleVo = rolesByIds.stream()
                    .filter(role -> role.getId().equals(perm.getIdentity().getId())).findFirst()
                    .orElse(null);
            if(ObjectUtils.isNotEmpty(roleVo)){
                specialRoles.add(TrackerSpecialRole.builder().trackerId(perm.getResourceId())
                        .specialRoleType(roleVo.getSpecialRoleType()).build());
            }
        });
        return specialRoles;
    }

    private List<GrantedPermission> loadTrackerFieldPermissions(Long userId, TrackerVo tracker
            , TrackerItemVo trackerItem, List<RoleVo> roles) {
        List<GrantedPermission> permissions = new ArrayList<>();
        tracker.getTrackerFields().forEach(trackerField -> {

            if (checkTrackerFieldPermission(userId, tracker, trackerItem, roles, permissions, trackerField)) {
                permissions.add(
                        GrantedPermission.builder().name(TrackerPermissions.FIELD_EDIT).resourceId(trackerField.getId())
                                         .build());
            }
        });
        return permissions;
    }

    private boolean checkTrackerFieldPermission(Long userId, TrackerVo tracker, TrackerItemVo trackerItem,
                                                List<RoleVo> roles,
                                                List<GrantedPermission> permissions, TrackerField trackerField) {
        FieldPermission fieldPermission = trackerField.getPermission();
        if (FieldPermission.UNRESTRICTED.equals(fieldPermission.getType())) {
            return true;
        } else if (FieldPermission.SINGLE.equals(fieldPermission.getType())) {

            List<BaseIdentity> baseIdentities = fieldPermission.getSinglePermissions();
            if (checkUserInIdentity(userId, baseIdentities, tracker, trackerItem, roles)) {
                return true;
            }

        } else if (FieldPermission.PER_STATUS.equals(fieldPermission.getType())) {
            List<BaseIdentity> baseIdentities =
                    fieldPermission.getStatusPermissions()
                                   .getOrDefault(trackerItem.getStatus().getId().toString(), new ArrayList<>());
            if (checkUserInIdentity(userId, baseIdentities, tracker, trackerItem, roles)) {
                return true;
            }
        } else if (FieldPermission.SAME_AS.equals(fieldPermission.getType())) {
            Long fieldId = fieldPermission.getSameAsField().getId();
            TrackerField referTo =
                    tracker.getTrackerFields().stream().filter(trackerField1 -> trackerField1.getId().equals(fieldId))
                           .findAny().orElse(null);
            if (referTo != null) {
                return checkTrackerFieldPermission(userId, tracker, trackerItem, roles, permissions, referTo);
            }
        }
        return false;
    }

    private List<GrantedPermission> loadTrackerPermissions(Long userId, TrackerVo tracker, TrackerItemVo trackerItem,
                                                           List<RoleVo> roles) {
        List<GrantedPermission> permissions = new ArrayList<>();
        List<PermissionGrantVo> trackerPermissions = tracker.getTrackerPermissions();

        for (PermissionGrantVo permissionGrantVo : trackerPermissions) {
            if (checkUserInIdentity(userId, permissionGrantVo.getGranted(), tracker, trackerItem, roles)) {
                permissions.add(
                        GrantedPermission.builder().name(permissionGrantVo.getPermissionName())
                                         .resourceId(trackerItem.getId())
                                         .build());
            }
        }
        return permissions;
    }

    private boolean checkUserInIdentity(Long userId, List<BaseIdentity> granted, TrackerVo tracker,
                                        TrackerItemVo trackerItem, List<RoleVo> roles) {
        for (BaseIdentity baseIdentity : granted) {
            if (baseIdentity.getType().equals(IdentityTypes.USER) && baseIdentity.getId().equals(userId)) {
                return true;
            } else if (baseIdentity.getType().equals(IdentityTypes.ROLE) && hasRole(roles, baseIdentity.getId())) {
                return true;
            } else if (baseIdentity.getType().equals(IdentityTypes.SPECIAL_ROLE) &&
                    hasSpecialRole(roles, baseIdentity.getId(), userId, tracker, trackerItem)) {
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

    private boolean hasSpecialRole(List<RoleVo> roles, Long identityId, Long userId, TrackerVo tracker,
                                   TrackerItemVo trackerItem) {
        RoleVo role = roleService.findOneRole(identityId);
        if (role == null || !role.getSpecialRole()) {
            return false;
        }
        if (SpecialRole.ALL_USERS.equals(role.getSpecialRoleType())) {
            return true;
        } else if (SpecialRole.PROJECT_OWNER.equals(role.getSpecialRoleType())) {
            ProjectEntity project = projectDao.findOneProject(tracker.getProjectId());
            return Objects.equals(project.getOwnerId(), userId);
        } else if (SpecialRole.PROJECT_ALL_MEMBERS.equals(role.getSpecialRoleType())) {
            return ObjectUtils.isNotEmpty(roles);
        } else if(ObjectUtils.isNotEmpty(trackerItem)){
            if (SpecialRole.TRACKER_CREATOR.equals(role.getSpecialRoleType())) {
                return Objects.equals(trackerItem.getCreateBy().getId(), userId);
            } else if (SpecialRole.TRACKER_OWNER.equals(role.getSpecialRoleType())) {
                return Objects.equals(trackerItem.getOwner().getId(), userId);
            } else if (SpecialRole.TRACKER_WATCHER.equals(role.getSpecialRoleType())) {
                return trackerItem.getWatchers().stream().anyMatch(watcher -> watcher.getId().equals(userId));
            }
        }

        return false;
    }

}
