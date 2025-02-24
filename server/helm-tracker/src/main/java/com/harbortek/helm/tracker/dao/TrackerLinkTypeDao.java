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
import com.harbortek.helm.tracker.entity.link.TrackerLinkTypeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class TrackerLinkTypeDao extends BaseJdbcDao {
    public TrackerLinkTypeEntity findById(Long enumId) {
        return findById(enumId, TrackerLinkTypeEntity.class);
    }

    public TrackerLinkTypeEntity findByCode(String code, Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerLinkTypeEntity.Fields.projectId).is(projectId));
        criteria = criteria.and(Criteria.where(TrackerLinkTypeEntity.Fields.code).is(code));
        Query query = Query.query(criteria);
        return findOne(query, TrackerLinkTypeEntity.class);
    }

    public List<TrackerLinkTypeEntity> findLinkTypes(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerLinkTypeEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        query.sort(Sort.by(TrackerLinkTypeEntity.Fields.ordinary).ascending());

        return find(query, TrackerLinkTypeEntity.class);
    }

    public void updateOrdinary(Collection<TrackerLinkTypeEntity> linkTypeEntities) {
        linkTypeEntities.forEach(linktype -> {
            Update update =
                     Update.update(TrackerLinkTypeEntity.Fields.ordinary, linktype.getOrdinary());
            updateById(linktype.getId(), update, TrackerLinkTypeEntity.class);
        });
    }


    public void batchCreateLinkTypes(Collection<TrackerLinkTypeEntity> entities) {
        saveAll(entities);
    }

    public void createLinkType(TrackerLinkTypeEntity entity) {
        save(entity);
    }

    public void updateLinkType(TrackerLinkTypeEntity entity) {
        save(entity);
    }

    public Object findOneLinkTypeByCode(Long projectId, String code) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerLinkTypeEntity.Fields.projectId).is(projectId));
        criteria = criteria.and(Criteria.where(TrackerLinkTypeEntity.Fields.code).is(code));
        Query query = Query.query(criteria);
        return findOne(query, TrackerLinkTypeEntity.class);
    }
}
