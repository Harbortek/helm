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
import com.harbortek.helm.system.entity.LogEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class LogDao extends BaseJdbcDao {


	public LogEntity create(LogEntity log) {
		return save(log);
	}

	public LogEntity update(LogEntity log) {
		return save(log);
	}

	public void delete(Long logId) {
		markAsDeleted(logId, LogEntity.class);
	}

	public LogEntity findById(Long logId) {
		return findById(logId, LogEntity.class);
	}

	public Page<LogEntity> findLogs(String keyword, Pageable pageable) {

		Criteria criteria =Criteria.where(BaseEntity.Fields.deleted).is(false);

		if (StringUtils.isNotEmpty(keyword)) {
			criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like(keyword));
			criteria = criteria.and(Criteria.where(BaseEntity.Fields.description).like(keyword));
		}
		Query query = Query.query(criteria).with(pageable);
		query.sort(Sort.by(Sort.Order.desc(BaseEntity.Fields.createDate)));
		return find(query,pageable, LogEntity.class);
	}
}
