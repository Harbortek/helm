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

import com.harbortek.helm.common.constants.CacheConstants;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.dao.RoleDao;
import com.harbortek.helm.system.entity.RoleEntity;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.service.RoleMemberService;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.util.*;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.parboiled.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleMemberService roleMemberService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    CacheManager cacheManager;

    @CacheEvict(value = CacheConstants.ROLE_CACHE_NAME, allEntries = true)
    public RoleVo createRole(RoleVo role) {
        if (!ObjectUtils.isValid(role.getId())) {
            role.setId(IDUtils.getId());
        }
        RoleEntity roleEntity = DataUtils.toEntity(role, RoleEntity.class);
        if (StringUtils.isEmpty(role.getScope())) {
            role.setScope(SpecialRole.SCOPE_GLOBAL);
        }
        RoleEntity entity = roleDao.create(roleEntity);

        LogUtils.log("系统管理", "角色管理", "角色 {0} 创建成功", role.getName());
        return DataUtils.toVo(entity, RoleVo.class);
    }

    @Override
    @CacheEvict(value = CacheConstants.ROLE_CACHE_NAME, allEntries = true)
    public void createRoles(List<RoleVo> roles) {
        for (RoleVo role : roles) {
            createRole(role);
        }
    }

    public RoleVo updateRole(RoleVo role) {
        RoleEntity oldRole = roleDao.findById(role.getId());
        oldRole.setName(role.getName());
        oldRole.setDescription(role.getDescription());
        RoleEntity entity = roleDao.update(oldRole);

        LogUtils.log("系统管理", "角色管理", "角色 {0} 更新成功", role.getName());
        return DataUtils.toVo(entity, RoleVo.class);
    }

    @CacheEvict(value = CacheConstants.ROLE_CACHE_NAME, allEntries = true)
    public void deleteRole(Long roleId) {
        RoleEntity role = roleDao.findById(roleId);
        roleDao.delete(roleId);

        roleMemberService.deleteRoleMembers(SpecialRole.SCOPE_GLOBAL, null, null, List.of(roleId));

        permissionService.unGrantByIdentityId(roleId);

        LogUtils.log("系统管理", "角色管理", "角色 {0} 删除成功", role.getName());
    }

    public RoleVo findOneRole(Long roleId) {
        return DataUtils.toVo(roleDao.findById(roleId), RoleVo.class);
    }

    @Override
    public List<RoleVo> findRolesByResourceId(Long ownerResourceId) {
        List<RoleVo> roles = PermissionCacheUtils.getRoles(ownerResourceId);
        if (roles!=null){
            return roles;
        }
        List<RoleEntity> roleEntities = roleDao.findRolesByResourceId(ownerResourceId);
        roleEntities.forEach(CacheUtils::put);

        roles = DataUtils.toVo(roleEntities, RoleVo.class);
        PermissionCacheUtils.putRoles(ownerResourceId, roles);
        return roles;
    }

    @Override
    public void deleteRolesByResourceId(@NotNull Long ownerResourceId) {
        List<RoleEntity> roleEntities = roleDao.findRolesByResourceId(ownerResourceId);
        List<Long> roleIds = ObjectUtils.ids(roleEntities);
        roleDao.deleteRoles(roleIds);
        roleMemberService.deleteRoleMembers(SpecialRole.SCOPE_GLOBAL, null, null, roleIds);
        roleIds.forEach(roleId -> {
            permissionService.unGrantByIdentityId(roleId);
        });
        PermissionCacheUtils.evictRoles(ownerResourceId);
    }

    @Override
    public List<RoleVo> findRolesByIds(List<Long> roleIds) {
        return DataUtils.toVo(roleDao.findByIds(roleIds, RoleEntity.class, true), RoleVo.class);
    }

//	@Override
//	public List<RoleVo> findSpecialRoles(String scope, String specialRoleType) {
//		return DataUtils.toVo(roleDao.findSpecialRoles(scope,specialRoleType),RoleVo.class);
//	}
//
//	@Override
//	public RoleVo findOneSpecialRole(String scope, Long resourceId, String specialRoleType) {
//		return DataUtils.toVo(roleDao.findOneSpecialRole(scope,resourceId,specialRoleType),RoleVo.class);
//	}

    @Override
    public List<RoleVo> findRoles(String scope, Long ownerResourceId) {
        List<RoleVo> roleVos = findRolesByResourceId(ownerResourceId);
        return roleVos.stream().filter(roleVo -> roleVo.getScope().equals(scope)).collect(Collectors.toList());
    }


    @Override
    public Boolean checkExistsByName(String name, String scope, Long ownerResourceId) {
        return roleDao.checkExistsByName(name, scope, ownerResourceId);
    }

    @Override
    public void deleteRolesByIds(List<Long> ids) {
        for (Long roleId : ids) {
            deleteRole(roleId);
        }
    }


}
