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
import com.harbortek.helm.system.entity.ParamEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class ParamDao extends BaseJdbcDao {

	public ParamEntity create(ParamEntity param) {
		return save(param);
	}

	public ParamEntity update(ParamEntity param) {
		return save(param);
	}

	public void delete(ParamEntity param) {
		markAsDeleted(param.getId(), ParamEntity.class);
	}

	public void batchDelete(List<Long> paramIds) {
		markAdDeleted(paramIds, ParamEntity.class);
	}

	public ParamEntity findById(Long paramId) {
		return findById(paramId, ParamEntity.class);
	}

	public Page<ParamEntity> findParams(String keyword, Pageable pageable) {

		Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
		if (StringUtils.isNotEmpty(keyword)) {
			criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like(keyword));
		}
		Query query = Query.query(criteria).with(pageable);
		return find(query,pageable, ParamEntity.class);
	}

	public Boolean checkExistsByName(String name) {
		Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
		criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).is(name));
		Query query = Query.query(criteria);
		List<ParamEntity> params = find(query, ParamEntity.class);
		return !params.isEmpty();
	}

	public Boolean checkExistsByCode(String code) {
		Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
		criteria = criteria.and(Criteria.where(ParamEntity.Fields.code).is(code));
		Query query = Query.query(criteria);
		List<ParamEntity> params = find(query,ParamEntity.class);
		return !params.isEmpty();
	}

}
