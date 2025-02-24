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

package com.harbortek.helm.productline.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.productline.entity.ReportEntity;
import com.harbortek.helm.productline.entity.ReportGroupEntity;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class ReportGroupDao extends BaseJdbcDao {
    public ReportGroupEntity createReportGroup(ReportGroupEntity reportGroup) {
        return save(reportGroup);
    }

    public ReportGroupEntity updateReportGroup(ReportGroupEntity reportGroup) {
        return save(reportGroup);
    }

    public void deleteReportGroup(Long id) {
        markAsDeleted(id, ReportGroupEntity.class);
    }

    public ReportGroupEntity findOneReportGroup(Long id) {
        return findById(id, ReportGroupEntity.class);
    }

    public List<ReportGroupEntity> findReportGroups(Long productLineId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ReportEntity.Fields.productLineId).is(productLineId));
        Query query = Query.query(criteria);
        return find(query, ReportGroupEntity.class);
    }

    public void deleteByProductLineId(Long productLineId) {
        Condition condition = DSL.noCondition();
        condition = condition.and(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE));
        condition = condition.and(getField(ReportGroupEntity.Fields.productLineId).eq(productLineId));
        getDslContext().update(getTable(ReportGroupEntity.class)).set(getField(BaseEntity.Fields.deleted), Boolean.TRUE).where(condition).execute();
    }
}
