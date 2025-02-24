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
import com.harbortek.helm.test.entity.TestReportEntity;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
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
public class TestReportDao extends BaseJdbcDao {

    public TestReportEntity createTestReport(TestReportEntity report){
        return save(report);
    }

    public void updateTestReport(TestReportEntity report){
        save(report);
    }

    public void deleteTestReport(Long id){
       markAsDeleted(id, TestReportEntity.class);
    }

    public Page<TestReportEntity> findTestReports(Long projectId, String keyword, Pageable pageable) {
        Criteria criteria = Criteria.empty();

        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        if (StringUtils.isNotEmpty(keyword)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like("%"+keyword+"%"));
        }
        criteria = criteria.and(Criteria.where(TestReportEntity.Fields.projectId).is(projectId));



        Query query = Query.query(criteria);
        if(pageable==null) {
            pageable = Pageable.unpaged();
        }
        query.with(pageable);
        if (ObjectUtils.isNotEmpty(pageable.getSort())) {
            pageable.getSort().forEach(item -> {
                if (item.getDirection() == Sort.Direction.DESC)
                    query.sort(Sort.by(item.getProperty()).descending());
                else
                    query.sort(Sort.by(item.getProperty()).ascending());
            });
            // 设置collation,将字符串数字按照数值处理
            //query.collation(Collation.of(Locale.CHINESE).numericOrdering(true));
        }


        return find(query,pageable, TestReportEntity.class);
    }
    public List<TestReportEntity> findByTestRunId(Long testRunId){
        String sql = "select * from test_reports where deleted = 'false' AND" +
                " json_search(test_run_ids,'one',(:testRunId))";

        Map<String,Object> params = new HashMap<String, Object>();
        params.put("testRunId", testRunId);

        return find(sql, params, TestReportEntity.class);
    }

    public TestReportEntity findById(Long id) {
        return findById(id, TestReportEntity.class);
    }


    public void batchCreateTestReports(List<TestReportEntity> testReports) {
        saveAll(testReports);
    }
}
