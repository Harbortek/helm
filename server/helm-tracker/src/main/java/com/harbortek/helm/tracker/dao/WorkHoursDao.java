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

package com.harbortek.helm.tracker.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.tracker.entity.log.WorkHoursEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class WorkHoursDao extends BaseJdbcDao {
    public WorkHoursEntity createWorkHours(WorkHoursEntity workHours) {
        return save(workHours);
    }

    public void batchCreateWorkHours(List<WorkHoursEntity> workHoursList) {
       saveAll(workHoursList);
    }

    public void saveWorkHours(WorkHoursEntity workHours) {
                save(workHours);
    }

    public List<WorkHoursEntity> findByObjectId(Long objectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(WorkHoursEntity.Fields.objectId).is(objectId));
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Order.desc(WorkHoursEntity.Fields.startTime)));
        return find(query, WorkHoursEntity.class);
    }
    public List<WorkHoursEntity> findByObjectIds(List<Long> objectIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(WorkHoursEntity.Fields.objectId).in(objectIds));
        Query query = Query.query(criteria);
        return find(query, WorkHoursEntity.class);
    }

    public WorkHoursEntity findById(Long id) {
        return findById(id,WorkHoursEntity.class);
    }

    public void deleteWorkHours(WorkHoursEntity workHours) {
        markAsDeleted(workHours.getId(), WorkHoursEntity.class);
    }

    public Double findRemainingWorkToday(Long memberId,Date beginDate) {
        String sql = "select sum(actual_hour) as sum from work_hours where deleted = false and member_id = :memberId " +
                "and " +
                "start_time >= :startTime and start_time <= :endTime";

        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);

        Map<String,Object> params = new HashMap<String, Object>();
        params.put("memberId", memberId);
        params.put("startTime", beginDate);
        params.put("endTime", date.getTime());

        return getValue(sql, params, Double.class);

    }

}
