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
import com.harbortek.helm.tracker.constants.PlanTypes;
import com.harbortek.helm.tracker.entity.plan.PlanDependencyEntity;
import com.harbortek.helm.tracker.entity.plan.PlanEntity;
import com.harbortek.helm.util.CacheUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@Slf4j
public class PlanDao extends BaseJdbcDao {

    public PlanEntity createPlan(PlanEntity plan) {
        return save(plan);
    }

    public PlanEntity updatePlan(PlanEntity plan) {
        return save(plan);
    }

    public void deletePlan(Long id) {
        markAsDeleted(id, PlanEntity.class);
    }

    public List<PlanEntity> findPlans(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(PlanEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Direction.ASC, PlanEntity.Fields.parentId, PlanEntity.Fields.ordinary));
        return find(query, PlanEntity.class);
    }

    public List<PlanEntity> findMilestones(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(PlanEntity.Fields.projectId).is(projectId));
        criteria = criteria.and(Criteria.where(PlanEntity.Fields.type).is(PlanTypes.TYPE_MILE_STONE));
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Direction.ASC, PlanEntity.Fields.planStartDate));
        return find(query, PlanEntity.class);
    }

    public PlanEntity findById(Long id) {
        return findById(id, PlanEntity.class);
    }

    public List<PlanEntity> findByParentId(Long parentId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(PlanEntity.Fields.parentId).is(parentId));
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Direction.ASC, PlanEntity.Fields.parentId, PlanEntity.Fields.ordinary));
        return find(query, PlanEntity.class);
    }


    public void batchUpdateSeqNumber(List<PlanEntity> data) {

        data.forEach(d -> {
            getDslContext().update(getTable(PlanEntity.class))
                           .set(getField(PlanEntity.Fields.seqNumber), d.getSeqNumber())
                           .where(getField(BaseEntity.Fields.id).eq(d.getId()))
                           .execute();

        });
        CacheUtils.evict(ObjectUtils.ids(data), PlanEntity.class);
    }

    public void updateDependencies(Long projectId, Long planId, Collection<PlanDependencyEntity> list) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(
                Criteria.where(PlanDependencyEntity.Fields.projectId).is(projectId));
        criteria = criteria.and(
                Criteria.where(PlanDependencyEntity.Fields.sourceId).is(planId).or(
                        Criteria.where(PlanDependencyEntity.Fields.targetId)
                                .is(planId)));
        Query query = Query.query(criteria);
        delete(query, PlanDependencyEntity.class);

        if (!list.isEmpty()) {
            saveAll(list);
        }
    }

    public List<PlanDependencyEntity> findPlanDependencies(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(PlanDependencyEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        return find(query, PlanDependencyEntity.class);
    }

    public List<PlanDependencyEntity> findPlanDependencies(Long projectId, Long planId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(
                Criteria.where(PlanDependencyEntity.Fields.projectId).is(projectId).and(

                        Criteria.where(PlanDependencyEntity.Fields.sourceId).is(planId).or(
                                Criteria.where(PlanDependencyEntity.Fields.targetId)
                                        .is(planId))));
        Query query = Query.query(criteria);
        return find(query, PlanDependencyEntity.class);
    }

    public void batchUpdateStartEndDate(Collection<PlanEntity> values) {
        values.forEach(d -> {
            getDslContext().update(getTable(PlanEntity.class))
                           .set(getField(PlanEntity.Fields.planStartDate), d.getPlanStartDate())
                           .set(getField(PlanEntity.Fields.planEndDate), d.getPlanEndDate())
                           .where(getField(BaseEntity.Fields.id).eq(d.getId()))
                           .execute();

        });
    }

    public void deletePlanDependencies(Long projectId, Long planId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(
                Criteria.where(PlanDependencyEntity.Fields.projectId).is(projectId).and(
                        Criteria.where(PlanDependencyEntity.Fields.sourceId).is(planId).or(
                                Criteria.where(PlanDependencyEntity.Fields.targetId)
                                        .is(planId))));
        Query query = Query.query(criteria);
        delete(query, PlanDependencyEntity.class);
    }

    public List<PlanEntity> findWaitExecutePlans(Long projectId, Long currentUserId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(PlanEntity.Fields.projectId).is(projectId));
        criteria = criteria.and(Criteria.where(PlanEntity.Fields.ownerId).is(currentUserId));
//        criteria = criteria.and(Criteria.where(PlanEntity.Fields.finished).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(PlanEntity.Fields.type).is(PlanTypes.TYPE_TASK));
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Direction.ASC, PlanEntity.Fields.itemNo));
        return find(query, PlanEntity.class);
    }

    public void batchChangeOrder(Collection<PlanEntity> planEntities) {
        planEntities.forEach(d -> {
            getDslContext().update(getTable(PlanEntity.class))
                    .set(getField(PlanEntity.Fields.ordinary), d.getOrdinary())
                    .set(getField(PlanEntity.Fields.parentId), d.getParentId())
                    .where(getField(BaseEntity.Fields.id).eq(d.getId()))
                    .execute();
        });
    }

    public void batchCreatePlans(List<PlanEntity> plans) {
        saveAll(plans);
    }

    public void deletePlansByProjectId(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(PlanEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        delete(query, PlanEntity.class);
    }

    public void deletePlanDependenciesByProjectId(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(PlanDependencyEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        delete(query, PlanDependencyEntity.class);
    }

    public void batchCreatePlanDependencies(List<PlanDependencyEntity> dependencies) {
        saveAll(dependencies);
    }
}
