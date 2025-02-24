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
import com.harbortek.helm.tracker.entity.plan.DeliverableEntity;
import com.harbortek.helm.util.CacheUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class DeliverableDao extends BaseJdbcDao {
    public DeliverableEntity createDeliverable(DeliverableEntity deliverable) {
        return save(deliverable);
    }

    public DeliverableEntity updateDeliverable(DeliverableEntity deliverable) {
        return save(deliverable);
    }

    public void deleteDeliverable(Long id) {
        markAsDeleted(id, DeliverableEntity.class);
    }

    public List<DeliverableEntity> findDeliverables(Long projectId, Long milestoneId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(DeliverableEntity.Fields.projectId).is(projectId));
        criteria = criteria.and(Criteria.where(DeliverableEntity.Fields.milestoneId).is(milestoneId));
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Direction.ASC, DeliverableEntity.Fields.ordinary));
        return find(query, DeliverableEntity.class);
    }

    public DeliverableEntity findById(Long deliverableId) {
        return findById(deliverableId, DeliverableEntity.class);
    }

    public void deleteDeliverableByPlanId(Long projectId, Long milestoneId) {
        Condition condition = DSL.noCondition();
        condition = condition.and(getField(DeliverableEntity.Fields.projectId).eq(projectId));
        condition = condition.and(getField(DeliverableEntity.Fields.milestoneId).eq(milestoneId));
        getDslContext().update(getTable(DeliverableEntity.class)).set(getField(BaseEntity.Fields.deleted), true).where(condition).execute();
        CacheUtils.evictAll(DeliverableEntity.class);
    }

    public List<DeliverableEntity> findDeliverables(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(DeliverableEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Direction.ASC, DeliverableEntity.Fields.ordinary));
        return find(query, DeliverableEntity.class);
    }

    public void batchUpdateDeliverables(List<DeliverableEntity> deliverableEntities) {

        deliverableEntities.forEach(d -> {
            Condition condition = DSL.noCondition();
            condition = condition.and(getField(BaseEntity.Fields.id).eq(d.getId()));
            getDslContext().update(getTable(DeliverableEntity.class))
                           .set(getField(BaseEntity.Fields.name), d.getName())
                           .set(getField(BaseEntity.Fields.deleted), d.getDeleted())
                           .set(getField(DeliverableEntity.Fields.ordinary), d.getOrdinary())
                           .set(getField(DeliverableEntity.Fields.projectId), d.getProjectId())
                           .set(getField(DeliverableEntity.Fields.milestoneId), d.getMilestoneId())
                           .set(getField(DeliverableEntity.Fields.type), d.getType())
                           .set(getField(DeliverableEntity.Fields.url), d.getUrl())
                           .set(getField(DeliverableEntity.Fields.attachmentId), d.getAttachmentId())
                           .set(getField(DeliverableEntity.Fields.committed), d.getCommitted())
                           .where(condition).execute();

        });
        CacheUtils.evict(ObjectUtils.ids(deliverableEntities), DeliverableEntity.class);
    }

    public void batchCreateDeliverables(List<DeliverableEntity> deliverables) {
        saveAll(deliverables);
    }
}
