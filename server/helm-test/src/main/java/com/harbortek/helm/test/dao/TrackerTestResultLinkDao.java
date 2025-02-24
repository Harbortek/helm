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
import com.harbortek.helm.test.entity.TrackerTestResultLinkEntity;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@Slf4j
public class TrackerTestResultLinkDao extends BaseJdbcDao {
    public void createTrackerTestResultLink(TrackerTestResultLinkEntity trackerTestResultLinkEntity) {
        trackerTestResultLinkEntity.setId(IDUtils.getId());
        save(trackerTestResultLinkEntity);
    }

    public void updateTrackerTestResultLink(TrackerTestResultLinkEntity trackerTestResultLinkEntity) {
        save(trackerTestResultLinkEntity);
    }

    public void deleteTrackerTestResultLink(Long id) {
        markAsDeleted(id, TrackerTestResultLinkEntity.class);
    }

    public List<TrackerTestResultLinkEntity> findByTestResultId(Long testResultId) {
        Criteria criteria =
                Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE)
                        .and(TrackerTestResultLinkEntity.Fields.testResultId).is(testResultId);
        Query query = Query.query(criteria);
        return find(query, TrackerTestResultLinkEntity.class);
    }

    public List<TrackerTestResultLinkEntity> findByTrackerItemId(Long itemId) {
        Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE)
                                    .and(TrackerTestResultLinkEntity.Fields.trackerItemId).is(itemId);
        Query query = Query.query(criteria);
        return find(query, TrackerTestResultLinkEntity.class);
    }

    public TrackerTestResultLinkEntity findById(Long id) {
        return findById(id, TrackerTestResultLinkEntity.class);
    }


    public void batchCreateTrackerTestResultLinks(Collection<TrackerTestResultLinkEntity> entities) {
       saveAll(entities);
    }

    public void deleteByTrackerItemId(Long itemId) {
        Criteria criteria =
                Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE)
                        .and(TrackerTestResultLinkEntity.Fields.trackerItemId).is(itemId);
        Query query = Query.query(criteria);

        delete(query, TrackerTestResultLinkEntity.class);
    }

    public List<TrackerTestResultLinkEntity> findByTrackerItemIds(List<Long> trackerItemIds) {
        Criteria criteria =
                Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE)
                        .and(TrackerTestResultLinkEntity.Fields.trackerItemId).in(trackerItemIds);
        Query query = Query.query(criteria);
        return find(query, TrackerTestResultLinkEntity.class);
    }

    public void deleteByTrackerItemIds(Long testResultId, List<Long> trackerItemIds) {
        Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE)
                                    .and(TrackerTestResultLinkEntity.Fields.testResultId).is(testResultId)
                                    .and(TrackerTestResultLinkEntity.Fields.trackerItemId).in(trackerItemIds);
        Query query = Query.query(criteria);
        delete(query, TrackerTestResultLinkEntity.class);
    }

    public List<TrackerTestResultLinkEntity> findByTestResultIds(Collection<Long> testResultIds) {
        Criteria criteria =
                Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE)
                        .and(TrackerTestResultLinkEntity.Fields.testResultId).in(testResultIds);
        Query query = Query.query(criteria);
        return find(query, TrackerTestResultLinkEntity.class);
    }
}
