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

package com.harbortek.helm.system.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.entity.PermissionEntity;
import com.harbortek.helm.system.entity.RoleEntity;
import com.harbortek.helm.system.entity.UserEntity;
import com.harbortek.helm.util.ObjectUtils;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PermissionDao extends BaseJdbcDao {
    public void createPermission(@NotNull PermissionEntity permission) {
        save(permission);
    }

    public void createPermissions(List<PermissionEntity> permissionEntities) {
        saveAll(permissionEntities);
    }

    public List<PermissionEntity> findPermissions(List<String> names, List<Long> identityIds,
                                                  String identityType, List<Long> resourceIds) {
        Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
        if (ObjectUtils.isNotEmpty(names)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).in(names));
        }
        if (ObjectUtils.isNotEmpty(identityIds)) {
            criteria = criteria.and(Criteria.where(PermissionEntity.Fields.identityId).in(identityIds));
        }
        if (StringUtils.isNotEmpty(identityType)) {
            criteria = criteria.and(Criteria.where(PermissionEntity.Fields.identityType).is(identityType));
        }
        if (ObjectUtils.isNotEmpty(resourceIds)) {
            criteria = criteria.and(Criteria.where(PermissionEntity.Fields.resourceId).in(resourceIds));
        } else if (resourceIds == null || resourceIds.isEmpty()) {    //resourceIds=new ArrayList<>(); 查询resourceId不存在情况
            criteria = criteria.and(Criteria.where(PermissionEntity.Fields.resourceId).isNull());
        }
        Query query = Query.query(criteria);
        return find(query, PermissionEntity.class);
    }


    public void deletePermission(String name, Long identityId, String identityType, Long resourceId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(false));
        if (StringUtils.isNotEmpty(name)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).is(name));
        }
        if (ObjectUtils.isValid(identityId)) {
            criteria = criteria.and(Criteria.where(PermissionEntity.Fields.identityId).in(identityId));
        }
        if (StringUtils.isNotEmpty(identityType)) {
            criteria = criteria.and(Criteria.where(PermissionEntity.Fields.identityType).is(identityType));
        }
        if (ObjectUtils.isValid(resourceId)) {
            criteria = criteria.and(Criteria.where(PermissionEntity.Fields.resourceId).in(resourceId));
        }
        Query query = Query.query(criteria);
        delete(query, PermissionEntity.class);
    }

    public void deletePermissions(List<String> names, List<Long> identityIds,
                                  String identityType, List<Long> resourceIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(false));
        if (ObjectUtils.isNotEmpty(names)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).in(names));
        }
        if (ObjectUtils.isNotEmpty(identityIds)) {
            criteria = criteria.and(Criteria.where(PermissionEntity.Fields.identityId).in(identityIds));
        }
        if (StringUtils.isNotEmpty(identityType)) {
            criteria = criteria.and(Criteria.where(PermissionEntity.Fields.identityType).is(identityType));
        }
        if (ObjectUtils.isNotEmpty(resourceIds)) {
            criteria = criteria.and(Criteria.where(PermissionEntity.Fields.resourceId).in(resourceIds));
        }
        Query query = Query.query(criteria);
        delete(query, PermissionEntity.class);
    }


    public BaseIdentity findIdentity(Long identityId, String identityType) {

        BaseIdentity baseIdentity = new BaseIdentity();
        baseIdentity.setId(identityId);
        baseIdentity.setType(identityType);
        if (IdentityTypes.ROLE.equals(identityType) ||
                IdentityTypes.SPECIAL_ROLE.equals(identityType)) {
            RoleEntity role = findById(identityId, RoleEntity.class);
            if (role != null) {
                baseIdentity.setName(role.getName());
            }
        } else if (IdentityTypes.USER.equals(identityType)) {
            UserEntity user = findById(identityId, UserEntity.class);
            if (user != null) {
                baseIdentity.setName(user.getName());
                baseIdentity.setIcon(user.getIcon());
                baseIdentity.setDescription("（" + user.getEmail() + "）");
            }
        }
        return baseIdentity;
    }


    public void deleteByResourceId(Long resourceId) {
        deletePermissions(null, null, null, List.of(resourceId));
    }
}
