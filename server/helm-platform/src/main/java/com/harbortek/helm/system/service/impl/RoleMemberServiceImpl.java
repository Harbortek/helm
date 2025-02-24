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
import com.harbortek.helm.system.dao.RoleMemberDao;
import com.harbortek.helm.system.entity.RoleMemberEntity;
import com.harbortek.helm.system.service.RoleMemberService;
import com.harbortek.helm.system.vo.RoleMemberVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.PermissionCacheUtils;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleMemberServiceImpl implements RoleMemberService {

    @Autowired
    RoleMemberDao roleMemberDao;

    @Autowired
    CacheManager cacheManager;

    @Override
    public void createRoleMember(RoleMemberVo roleMemberVo) {
        if (!ObjectUtils.isValid(roleMemberVo.getId())) {
            roleMemberVo.setId(IDUtils.getId());
        }
        roleMemberDao.createRoleMember(DataUtils.toEntity(roleMemberVo, RoleMemberEntity.class));


        PermissionCacheUtils.evictRoleMembers(roleMemberVo.getOwnerResourceId());
    }


    @Override
    public void createRoleMembers(List<RoleMemberVo> roleMembers) {
        if (ObjectUtils.isEmpty(roleMembers)) {
            return;
        }
        roleMembers.forEach(r -> {
            if (!ObjectUtils.isValid(r.getId())) {
                r.setId(IDUtils.getId());
            }
        });
        roleMemberDao.createRoleMembers(DataUtils.toEntity(roleMembers, RoleMemberEntity.class));

        List<Long> resourceIds = roleMembers.stream().map(RoleMemberVo::getOwnerResourceId).distinct().toList();
        resourceIds.forEach(PermissionCacheUtils::evictRoleMembers);
    }


    @Override
    public void deleteRoleMember(Long userId, Long roleId) {

        List<RoleMemberEntity> roleMembers = roleMemberDao.findRoleMembers(null, null, List.of(userId),
                                                                           List.of(roleId));
        roleMemberDao.deleteRoleMember(userId, roleId);

        List<Long> resourceIds = roleMembers.stream().map(RoleMemberEntity::getOwnerResourceId).distinct().toList();
        resourceIds.forEach(PermissionCacheUtils::evictRoleMembers);
    }

    @Override
    public void deleteRoleMembers(String scope, Long ownerResourceId, List<Long> userIds, List<Long> roleIds) {
        roleMemberDao.deleteRoleMembers(scope, ownerResourceId, userIds, roleIds);
        PermissionCacheUtils.evictRoleMembers(ownerResourceId);
    }

    @Override
    public Map<Long, Integer> countRoleMembersGroupByRoleId(String scope, Long ownerResourceId,
                                                            List<Long> userIds,
                                                            List<Long> roleIds) {
        return roleMemberDao.countRoleMembersGroupByRoleId(scope, ownerResourceId, userIds, roleIds);
    }


    @Override
    public List<RoleMemberVo> findRoleMembers(String scope, Long ownerResourceId, List<Long> userIds,
                                              List<Long> roleIds) {

        return DataUtils.toVo(roleMemberDao.findRoleMembers(scope, ownerResourceId, userIds, roleIds),
                              RoleMemberVo.class);
    }

    public List<RoleMemberVo> findRoleMembersByResourceId(@NotNull Long resourceId) {
        List<RoleMemberVo> roleMemberVos = PermissionCacheUtils.getRoleMembers(resourceId);
        if (roleMemberVos == null) {
            roleMemberVos =
                    DataUtils.toVo(roleMemberDao.findRoleMembers(null, resourceId, null, null), RoleMemberVo.class);
            PermissionCacheUtils.putRoleMembers(resourceId, roleMemberVos);
        }
        return roleMemberVos;
    }

    public List<RoleMemberVo> findSystemRoleMembers() {
        List<RoleMemberVo> roleMemberVos = PermissionCacheUtils.getRoleMembers(null);
        if (roleMemberVos == null) {
            roleMemberVos =
                    DataUtils.toVo(roleMemberDao.findRoleMembers(SpecialRole.SCOPE_GLOBAL, null
                                           , null, null),
                                   RoleMemberVo.class);

            Objects.requireNonNull(cacheManager.getCache(CacheConstants.ROLE_CACHE_NAME)).put("GLOBAL", roleMemberVos);
        }
        return roleMemberVos;
    }

    @Override
    public List<RoleMemberVo> findRoleMembersByResourceIdAndUserId(@NotNull String scope, Long resourceId,
                                                                   @NotNull Long userId) {
        List<RoleMemberVo> roleMemberVos =
                resourceId != null ? findRoleMembersByResourceId(resourceId) : findSystemRoleMembers();
        return roleMemberVos.stream().filter(r -> Objects.equals(r.getUserId(), userId) && Objects.equals(r.getScope()
                                    , scope))
                            .collect(Collectors.toList());
    }

    @Override
    public List<RoleMemberVo> findSystemRoleMembersByUserId(Long userId) {
        List<RoleMemberVo> roleMemberVos = findSystemRoleMembers();

        return roleMemberVos.stream().filter(r -> Objects.equals(r.getUserId(), userId)).toList();
    }
}
