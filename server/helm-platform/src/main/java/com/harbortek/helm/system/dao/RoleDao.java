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
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.entity.RoleEntity;
import com.harbortek.helm.util.ObjectUtils;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class RoleDao extends BaseJdbcDao {

	public RoleEntity create(RoleEntity role) {
		return save(role);
	}
	public RoleEntity update(RoleEntity role) {
		return save(role);
	}

	public void delete(Long roleId) {
		markAsDeleted(roleId, RoleEntity.class);
	}

	public RoleEntity findById(Long roleId) {
		return findById(roleId, RoleEntity.class);
	}

//	public List<RoleEntity> findRoles(String scope,Long ownerResourceId) {
//
//		Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
//		if (StringUtils.isNotEmpty(scope)) {
//			criteria = criteria.and(Criteria.where(RoleEntity.Fields.scope).is(scope));
//		}else{
//			criteria = criteria.and(Criteria.where(RoleEntity.Fields.scope).is(SpecialRole.SCOPE_GLOBAL));
//		}
//		if (ObjectUtils.isValid(ownerResourceId)) {
//			criteria = criteria.and(Criteria.where(RoleEntity.Fields.ownerResourceId).is(ownerResourceId));
//		}else{
//			criteria = criteria.and(Criteria.where(RoleEntity.Fields.ownerResourceId).isNull());
//		}
//		Query query = Query.query(criteria);
//		return find(query, RoleEntity.class);
//	}


	public Boolean checkExistsByName(String name,String scope,Long ownerResourceId) {
		Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
		criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).is(name));
		if (StringUtils.isNotEmpty(scope)) {
			criteria = criteria.and(Criteria.where(RoleEntity.Fields.scope).is(scope));
		}else{
			criteria = criteria.and(Criteria.where(RoleEntity.Fields.scope).is(SpecialRole.SCOPE_GLOBAL));
		}
		if (ObjectUtils.isValid(ownerResourceId)) {
			criteria = criteria.and(Criteria.where(RoleEntity.Fields.ownerResourceId).is(ownerResourceId));
		}else{
			criteria = criteria.and(Criteria.where(RoleEntity.Fields.ownerResourceId).isNull());
		}
		Query query = Query.query(criteria);
		List<RoleEntity> roles = find(query, RoleEntity.class);
		return !roles.isEmpty();
	}

	public void deleteRoles(List<Long> ids) {
		markAdDeleted(ids, RoleEntity.class);
	}




	public List<RoleEntity> findRolesByResourceId(Long ownerResourceId) {

		Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
		if (ObjectUtils.isValid(ownerResourceId)) {
			criteria = criteria.and(Criteria.where(RoleEntity.Fields.ownerResourceId).is(ownerResourceId));
		}else{
			criteria = criteria.and(Criteria.where(RoleEntity.Fields.ownerResourceId).isNull());
		}
		Query query = Query.query(criteria).sort(Sort.by(Sort.Direction.ASC, BaseEntity.Fields.createDate));
		return find(query, RoleEntity.class);
	}

//    public List<RoleEntity> findSpecialRoles(String scope, String specialRoleType) {
//		Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
//		criteria = criteria.and(Criteria.where(RoleEntity.Fields.specialRole).is(true));
//		if (StringUtils.isNotEmpty(scope)) {
//			criteria = criteria.and(Criteria.where(RoleEntity.Fields.scope).is(scope));
//		}
//
//		if (StringUtils.isNotEmpty(specialRoleType)){
//			criteria = criteria.and(Criteria.where(RoleEntity.Fields.specialRoleType).is(specialRoleType));
//		}
//		Query query = Query.query(criteria).sort(Sort.by(Sort.Direction.ASC, BaseEntity.Fields.createDate));
//		return find(query, RoleEntity.class);
//    }
//
//	public RoleEntity findOneSpecialRole(@NotNull String scope, @NotNull Long ownerResourceId,
//										 @NotNull String specialRoleType) {
//		Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
//		criteria = criteria.and(Criteria.where(RoleEntity.Fields.specialRole).is(true));
//		if (StringUtils.isNotEmpty(scope)) {
//			criteria = criteria.and(Criteria.where(RoleEntity.Fields.scope).is(scope));
//		}
//		if (ObjectUtils.isValid(ownerResourceId)) {
//			criteria = criteria.and(Criteria.where(RoleEntity.Fields.ownerResourceId).is(ownerResourceId));
//		}
//
//		if (StringUtils.isNotEmpty(specialRoleType)){
//			criteria = criteria.and(Criteria.where(RoleEntity.Fields.specialRoleType).is(specialRoleType));
//		}
//		Query query = Query.query(criteria).sort(Sort.by(Sort.Direction.ASC, BaseEntity.Fields.createDate));
//		return findOne(query, RoleEntity.class);
//	}
}
