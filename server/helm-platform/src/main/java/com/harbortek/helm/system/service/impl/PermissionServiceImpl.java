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
import com.harbortek.helm.common.config.module.PermissionLoader;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.dao.PermissionDao;
import com.harbortek.helm.system.entity.PermissionEntity;
import com.harbortek.helm.system.entity.RoleEntity;
import com.harbortek.helm.system.entity.UserEntity;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.vo.GrantedPermission;
import com.harbortek.helm.system.vo.PermissionVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.PermissionCacheUtils;
import com.harbortek.helm.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    PermissionDao permissionDao;

    @Autowired
    ModuleManager moduleManager;


    @Override
    public boolean hasPermission(String permission, Long resourceId) {
        List<GrantedPermission> grantedPermissions = loadPermissions(SecurityUtils.getCurrentUser());
        for (GrantedPermission grantedPermission : grantedPermissions) {
            if (grantedPermission.getName().equals(permission)) {
                if (resourceId != null) {
                    if (resourceId.equals(grantedPermission.getResourceId())) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void grant(String permission, BaseIdentity identity, Long resourceId) {
        PermissionEntity permissionEntity =
                PermissionEntity.builder().id(IDUtils.getId()).resourceId(resourceId).name(permission)
                                .identityId(identity.getId()).identityType(identity.getType()).build();
        permissionDao.createPermission(permissionEntity);
        PermissionCacheUtils.evictGrantedPermissions(resourceId);
    }

    @Override
    public void grant(String permission, List<BaseIdentity> granted, Long resourceId) {
        List<PermissionEntity> permissionEntities = granted.stream().map(grantedIdentity -> {
            PermissionEntity permissionEntity = new PermissionEntity();
            permissionEntity.setId(IDUtils.getId());
            permissionEntity.setName(permission);
            permissionEntity.setIdentityId(grantedIdentity.getId());
            permissionEntity.setIdentityType(grantedIdentity.getType());
            permissionEntity.setResourceId(resourceId);
            return permissionEntity;
        }).collect(Collectors.toList());
        permissionDao.createPermissions(permissionEntities);
        PermissionCacheUtils.evictGrantedPermissions(resourceId);
    }

    @Override
    public void grant(List<PermissionVo> permissions) {
        List<PermissionEntity> permissionEntities = permissions.stream().map(permissionVo -> {
            PermissionEntity permissionEntity = new PermissionEntity();
            permissionEntity.setId(permissionVo.getId());
            permissionEntity.setName(permissionVo.getName());
            permissionEntity.setIdentityId(permissionVo.getIdentity().getId());
            permissionEntity.setIdentityType(permissionVo.getIdentity().getType());
            permissionEntity.setResourceId(permissionVo.getResourceId());
            return permissionEntity;
        }).collect(Collectors.toList());
        permissionDao.createPermissions(permissionEntities);

        Set<Long> resourceIds =
                permissionEntities.stream().map(PermissionEntity::getResourceId).collect(Collectors.toSet());
        resourceIds.forEach(PermissionCacheUtils::evictGrantedPermissions);
    }

    @Override
    public void unGrant(String permission, BaseIdentity identity, Long resourceId) {
        permissionDao.deletePermission(permission, identity.getId(), identity.getType(), resourceId);
        PermissionCacheUtils.evictGrantedPermissions(resourceId);
    }

    @Override
    public void unGrantByIdentityId(Long roleId) {
        permissionDao.deletePermissions(null, List.of(roleId), IdentityTypes.ROLE, null);
        PermissionCacheUtils.evictGrantedPermissions(null);
    }

    @Override
    public void unGrantByResourceId(Long resourceId) {
        permissionDao.deleteByResourceId(resourceId);
        PermissionCacheUtils.evictGrantedPermissions(resourceId);
    }

    @Override
    public void unGrantByIdentityIdAndResourceIds(Long identityId, List<Long> resourceIds) {
        //页面权限
        permissionDao.deletePermissions(null, List.of(identityId), null, resourceIds);
        resourceIds.forEach(PermissionCacheUtils::evictGrantedPermissions);
    }


    @Override
    public List<PermissionVo> findPermissions(List<String> names, List<Long> identityIds,
                                              String identityType,
                                              List<Long> resourceIds) {
        List<PermissionVo> permissionVos = new ArrayList<>();
        if (resourceIds != null && !resourceIds.isEmpty()) {
            permissionVos = findPermissionsByResourceIds(resourceIds);
        }else{
            permissionVos = findPermissionsByResourceId(null);
        }

        if (names != null && !names.isEmpty()) {
            permissionVos = permissionVos.stream().filter(permissionVo -> names.contains(permissionVo.getName()))
                                         .collect(Collectors.toList());
        }

        if (identityIds != null && !identityIds.isEmpty()) {
            permissionVos = permissionVos.stream().filter(permissionVo -> identityIds.contains(
                                                 permissionVo.getIdentity().getId()))
                                         .collect(Collectors.toList());
        }

        if (identityType != null) {
            permissionVos = permissionVos.stream().filter(permissionVo -> identityType.equals(
                                                 permissionVo.getIdentity().getType()))
                                         .collect(Collectors.toList());
        }
        return permissionVos;
    }

    @Override
    public List<PermissionVo> findPermissionsByResourceId(Long resourceId) {
        List<PermissionVo> permissionVos = PermissionCacheUtils.getPermissions(resourceId);
        if (permissionVos != null) {
            return permissionVos;
        }

        List<PermissionEntity> permissionEntities = permissionDao.findPermissions(null, null,
                                                                                  null,
                                                                                  resourceId != null ?
                                                                                          List.of(resourceId) : null);

        permissionVos = convertToVo(permissionEntities);
        PermissionCacheUtils.putPermissions(resourceId, permissionVos);

        return permissionVos;
    }

    @Override
    public List<PermissionVo> findPermissionsByResourceIds(@NotNull List<Long> resourceIds) {
        List<Long> needsToLoad = new ArrayList<>();
        List<PermissionVo> allPermissionVos = new ArrayList<>();

        for (Long resourceId : resourceIds) {
            List<PermissionVo> permissionVos = PermissionCacheUtils.getPermissions(resourceId);
            if (permissionVos == null) {
                needsToLoad.add(resourceId);
            } else {
                allPermissionVos.addAll(permissionVos);
            }
        }

        if (!needsToLoad.isEmpty()) {
            List<PermissionEntity> permissionEntities = permissionDao.findPermissions(null, null,
                                                                                      null,
                                                                                      needsToLoad);
            allPermissionVos.addAll(convertToVo(permissionEntities));
        }

        Map<Long, List<PermissionVo>> map =
                allPermissionVos.stream().collect(Collectors.groupingBy(PermissionVo::getResourceId));

        map.forEach(PermissionCacheUtils::putPermissions);


        return allPermissionVos;
    }

    @Override
    public List<PermissionVo> findGlobalPermissionsByIdentityId(Long identityId) {
        List<PermissionVo> permissionVos = findPermissionsByResourceId(null);
        return permissionVos.stream().filter(permissionVo -> permissionVo.getIdentity().getId().equals(identityId))
                            .collect(Collectors.toList());
    }

    @Override
    public List<PermissionVo> findGlobalPermissionsByIdentities(List<Long> identityIds, String identityType) {

        List<PermissionVo> permissionVos = findPermissionsByResourceId(null);
        return permissionVos.stream().filter(permissionVo -> identityIds.contains(permissionVo.getIdentity().getId()) &&
                                    identityType.equals(permissionVo.getIdentity().getType()))
                            .collect(Collectors.toList());
    }

    @Override
    public List<GrantedPermission> loadPermissions(UserVo user) {
        List<GrantedPermission> grantedPermissions = new ArrayList<>();
        for (PermissionLoader loader : moduleManager.getPermissionLoaders()) {
            LocalDateTime start = LocalDateTime.now();
            List<GrantedPermission> permissions = loader.loadPermissions(user);
            if (permissions != null) {
                grantedPermissions.addAll(permissions);
            }
            LocalDateTime end = LocalDateTime.now();
            long millis = Math.abs(end.until(start, ChronoUnit.MILLIS));
            log.info("load {} permissions cost {} ms", loader.getClass().getName(), millis);
        }
        return grantedPermissions;
    }

    @NotNull
    private List<PermissionVo> convertToVo(List<PermissionEntity> permissionEntities) {
        List<Long> roleIds =
                permissionEntities.stream().filter(i -> IdentityTypes.ROLE.equals(i.getIdentityType()))
                                  .map(PermissionEntity::getIdentityId).distinct().toList();
        List<Long> userIds =
                permissionEntities.stream().filter(i -> IdentityTypes.USER.equals(i.getIdentityType())).map(
                        PermissionEntity::getIdentityId).distinct().toList();
        //加载角色和用户进缓存
        if (!roleIds.isEmpty()) {
            permissionDao.findByIds(roleIds, RoleEntity.class, true);
        }
        if (!userIds.isEmpty()) {
            permissionDao.findByIds(userIds, UserEntity.class, true);
        }

        return permissionEntities.stream().map(permissionEntity -> {
            PermissionVo permissionVo = new PermissionVo();
            permissionVo.setId(permissionEntity.getId());
            permissionVo.setName(permissionEntity.getName());
            permissionVo.setResourceId(permissionEntity.getResourceId());

            BaseIdentity baseIdentity = permissionDao.findIdentity(permissionEntity.getIdentityId(),
                                                                   permissionEntity.getIdentityType());
            permissionVo.setIdentity(baseIdentity);
            return permissionVo;
        }).collect(Collectors.toList());
    }
}
