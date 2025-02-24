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

package com.harbortek.helm.test.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.test.entity.TestRunEntity;
import com.harbortek.helm.tracker.util.FilterUtils;
import com.harbortek.helm.tracker.vo.view.ObjectFilter;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class TestRunDao extends BaseJdbcDao {

    public TestRunEntity createTestRun(TestRunEntity plan){
        return save(plan);
    }

    public void updateTestRun(TestRunEntity plan){
        save(plan);
    }

    public void deleteTestRun(Long id){
       markAsDeleted(id, TestRunEntity.class);
    }

    public Page<TestRunEntity> findTestRuns(Long projectId, Long statusId, String keyword, ObjectFilter filter, Pageable pageable){
        Criteria criteria = Criteria.empty();

        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        if(ObjectUtils.isNotEmpty(statusId)){
            criteria = criteria.and(Criteria.where(TestRunEntity.Fields.statusId).is(statusId));
        }
        if (StringUtils.isNotEmpty(keyword)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like("%"+keyword+"%"));
        }
        criteria = criteria.and(Criteria.where(TestRunEntity.Fields.projectId).is(projectId));

        if (filter != null) {
            criteria = criteria.and(FilterUtils.getCriteria(filter));
        }
        Query query = Query.query(criteria);
        query.with(Objects.requireNonNullElseGet(pageable, Pageable::unpaged));
        if (pageable!=null && ObjectUtils.isNotEmpty(pageable.getSort())) {
            pageable.getSort().forEach(item -> {
                if (item.getDirection() == Sort.Direction.DESC)
                    query.sort(Sort.by(item.getProperty()).descending());
                else
                    query.sort(Sort.by(item.getProperty()).ascending());
            });
            // 设置collation,将字符串数字按照数值处理
//            query.collation(Collation.of(Locale.CHINESE).numericOrdering(true));
        }

        return find(query, pageable,TestRunEntity.class);
    }

    public List<TestRunEntity> findTestRunsByIds(List<Long> ids){
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).in(ids));
        Query query = Query.query(criteria);
        return find(query, TestRunEntity.class);
    }

    public TestRunEntity findById(Long id) {
        return findById(id, TestRunEntity.class);
    }

    public void batchCreateTestRuns(List<TestRunEntity> testRunList) {
        saveAll(testRunList);
    }

    public List<TestRunEntity> findByIds(List<Long> testRunIds) {
        return findByIds(testRunIds, TestRunEntity.class);
    }


    public List<Long> findTestCaseIds(Long runId) {
        String sql="select test_case_id from test_results where deleted=0 and test_run_id="+runId;
        return findIds(sql, null);
    }
}
