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

import cn.hutool.core.util.StrUtil;
import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.test.entity.TestReportEntity;
import com.harbortek.helm.test.entity.TestResultEntity;
import com.harbortek.helm.test.vo.TestResultStat;
import com.harbortek.helm.tracker.util.FilterUtils;
import com.harbortek.helm.tracker.vo.view.ObjectFilter;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.UpdateSetMoreStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class TestResultDao extends BaseJdbcDao {
    public TestResultEntity createTestResult(TestResultEntity testResult) {
        return save(testResult);
    }

    public void updateTestResult(TestResultEntity testResult) {
        save(testResult);
    }

    public void deleteTestResult(Long id) {
        markAsDeleted(id, TestResultEntity.class);
    }

    public Page<TestResultEntity> findTestResults(Long testRunId, ObjectFilter filter, String keyword,
                                                  Pageable pageable) {
        Criteria criteria = Criteria.empty();

        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        if (StringUtils.isNotEmpty(keyword)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like("%"+keyword+"%"));
        }
        criteria = criteria.and(Criteria.where(TestResultEntity.Fields.testRunId).is(testRunId));
        if (filter != null) {
            criteria = criteria.and(FilterUtils.getCriteria(filter));
        }
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }

        String sql="SELECT tr.* FROM `test_results` tr " +
                "left join tracker_items ti on ti.id = tr.test_case_id where ";
        sql+= StrUtil.toUnderlineCase(criteria.toString())
                .replace("deleted","tr.deleted").replace("()","(1=1)");
        String countSql=sql;
        if(ObjectUtils.isNotEmpty(pageable.getSort())){
            int i=1;
            for (Sort.Order order : pageable.getSort()) {
                if(i++==1){
                    sql+=" order by ";
                }else{
                    sql+=" , ";
                }
                sql += StrUtil.toUnderlineCase(order.getProperty())+" "+order.getDirection();
            }
        }
        sql=sql.replace("owner","ti.owner").replace("priority","ti.priority")
                .replace("name","tr.name");
        countSql=countSql.replace("tr.*","count(*)").replace("owner","ti.owner")
                .replace("priority","ti.priority").replace("name","tr.name");
        if(ObjectUtils.isNotEmpty(pageable.getOffset())){
            sql+=" limit "+pageable.getOffset()+","+pageable.getPageSize();
        }
        List<TestResultEntity> entityList = find(sql, new HashMap<>(), TestResultEntity.class);

        Long count = count(countSql, new HashMap<>());

        System.out.println("sql:"+sql);
        return new PageImpl<>(entityList,pageable,count);
//        return find(query,pageable, TestResultEntity.class);
    }

    public List<TestResultEntity> findTestResultsByTestRunId(Long testRunId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TestResultEntity.Fields.testRunId).is(testRunId));
        Query query = Query.query(criteria);
        return find(query, TestResultEntity.class);
    }

    public List<TestResultEntity> findTestResultsByTrackerItemId(Long trackerItemId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TestResultEntity.Fields.testCaseId).is(trackerItemId));
        Query query = Query.query(criteria);
        return find(query, TestResultEntity.class);
    }

    public List<TestResultEntity> findTestResultsByIds(List<Long> ids) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).in(ids));
        Query query = Query.query(criteria);
        return find(query, TestResultEntity.class);
    }

    public TestResultEntity findById(Long id) {
        return findById(id, TestResultEntity.class);
    }


    public void batchCreateTestResults(List<TestResultEntity> TestResultList) {
        saveAll(TestResultList);
    }


    public void deleteTestResultsByTestCaseIds(List<Long> testCaseIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TestResultEntity.Fields.testCaseId).in(testCaseIds));
        Query query = Query.query(criteria);
        delete(query, TestResultEntity.class);
    }

    public void batchUpdateTestResults(List<TestResultEntity> updates) {

        for (TestResultEntity testResultEntity : updates) {
            UpdateSetMoreStep<?> moreStep = getDslContext().update(getTable(TestResultEntity.class))
                    .set(getField(TestResultEntity.Fields.resultId), testResultEntity.getResultId());
            if(ObjectUtils.isNotEmpty(testResultEntity.getResultDesc())){
                moreStep.set(getField(TestResultEntity.Fields.resultDesc), testResultEntity.getResultDesc());
            }
//            if(ObjectUtils.isNotEmpty(testResultEntity.getTestSteps())){
//                moreStep.set(getField(TestResultEntity.Fields.testSteps), testResultEntity.getTestSteps());
//            }
            if(ObjectUtils.isNotEmpty(testResultEntity.getTestStepResults())){
                moreStep.set(getField(TestResultEntity.Fields.testStepResults), testResultEntity.getTestStepResults());
            }
            if(ObjectUtils.isNotEmpty(testResultEntity.getExecutorId())){
                moreStep.set(getField(TestResultEntity.Fields.executorId), testResultEntity.getExecutorId());
            }
            moreStep.where(getField(BaseEntity.Fields.id).eq(testResultEntity.getId()))
                    .execute();
        }
    }


    public Integer countTestResultsByTestRunId(Long testRunId) {
        long count = count(Query.query(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE)
                                               .and(TestResultEntity.Fields.testRunId)
                                               .is(testRunId)), TestResultEntity.class);
        return (int) count;
    }

    public Integer countExecutedTestResultsByTestRunId(Long testRunId) {
        long count = count(Query.query(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE)
                                               .and(TestResultEntity.Fields.testRunId).is(testRunId)
                                               .and(TestResultEntity.Fields.resultId).isNotNull()),
                           TestResultEntity.class);
        return (int) count;
    }

    public Integer countPassedTestResultsByTestRunId(Long testRunId, Long resultId) {
        long count = count(Query.query(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE)
                                               .and(TestResultEntity.Fields.testRunId)
                                               .is(testRunId).and(TestResultEntity.Fields.resultId)
                                               .is(resultId)), TestResultEntity.class);
        return (int) count;
    }

    public Integer countTestResultsByTestReportId(Long reportId) {
        List<Long> testRunIds = findById(reportId, TestReportEntity.class).getTestRunIds();
        long count = count(Query.query(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE)
                                               .and(TestResultEntity.Fields.testRunId)
                                               .in(testRunIds)), TestResultEntity.class);
        return (int) count;
    }

    public Integer countPassedTestResultsByTestReportId(Long reportId, Long resultId) {
        List<Long> testRunIds = findById(reportId, TestReportEntity.class).getTestRunIds();
        long count = count(Query.query(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE)
                                               .and(TestResultEntity.Fields.testRunId)
                                               .in(testRunIds).and(TestResultEntity.Fields.resultId)
                                               .is(resultId)), TestResultEntity.class);
        return (int) count;
    }

    public List<TestResultEntity> findByTestRunIds(List<Long> testRunIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TestResultEntity.Fields.testRunId).in(testRunIds));
        Query query = Query.query(criteria);
        return find(query, TestResultEntity.class);
    }

    public List<TestResultStat> countTestResultsGroupByResult(List<Long> testRunIds) {
        String sql = "select result_id as id, count(*) as count from test_results where deleted=0 and test_run_id in " +
                "(:testRunIds) " +
                "group " +
                "by " +
                "result_id";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("testRunIds", testRunIds);

        return find(sql, params, TestResultStat.class);

    }
}
