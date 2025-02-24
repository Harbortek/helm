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
import com.harbortek.helm.tracker.entity.code.TrackerMergeRequestEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class TrackerMergeRequestDao extends BaseJdbcDao {

    public void createMergeRequest(TrackerMergeRequestEntity mergeRequestEntity) {
        save(mergeRequestEntity);
    }

    public TrackerMergeRequestEntity findByMergeRequestId(Long projectId,String mergeRequestId){
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerMergeRequestEntity.Fields.projectId).is(projectId));
        criteria = criteria.and(Criteria.where(TrackerMergeRequestEntity.Fields.mergeRequestId).is(mergeRequestId));
        Query query = Query.query(criteria);
        return findOne(query, TrackerMergeRequestEntity.class);
    }

    public void updateMergeRequest(TrackerMergeRequestEntity mergeRequestEntity) {
        save(mergeRequestEntity);
    }

    public List<TrackerMergeRequestEntity> findByItemId(Long itemId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerMergeRequestEntity.Fields.itemId).is(itemId));
        Query query = Query.query(criteria);
        return find(query, TrackerMergeRequestEntity.class);
    }
}
