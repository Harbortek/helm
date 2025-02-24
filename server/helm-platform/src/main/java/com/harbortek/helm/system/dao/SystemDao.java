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
import com.harbortek.helm.system.entity.SystemEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class SystemDao extends BaseJdbcDao {

	public SystemEntity create(SystemEntity system) {
		return save(system);
	}

	public SystemEntity update(SystemEntity system) {
		return save(system);
	}

	public void delete(Long systemId) {
		markAsDeleted(systemId, SystemEntity.class);
	}

	public SystemEntity findById(Long id) {
		return findById(id, SystemEntity.class);
	}

	public Page<SystemEntity> findSystems(Pageable pageable) {
		Query query = Query.query(Criteria.where(BaseEntity.Fields.deleted).is(false)).with(pageable);
		return find(query,pageable, SystemEntity.class);
	}

	public List<SystemEntity> findAllSystems(String keyword) {
		Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
		if (StringUtils.isNotEmpty(keyword)) {
			criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like(keyword));
		}

		Query query = Query.query(criteria).sort(Sort.by(Sort.Direction.ASC, BaseEntity.Fields.createDate));
		return find(query, SystemEntity.class);
	}

	public Boolean checkExistsByName(String name) {
		Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
		criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).is(name));
		Query query = Query.query(criteria);
		List<SystemEntity> systems = find(query, SystemEntity.class);
		return !systems.isEmpty();
	}

}
