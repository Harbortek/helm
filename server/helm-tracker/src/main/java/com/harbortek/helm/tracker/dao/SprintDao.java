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
import com.harbortek.helm.tracker.entity.plan.PlanEntity;
import com.harbortek.helm.tracker.entity.plan.SprintEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class SprintDao extends BaseJdbcDao {

    public SprintEntity createSprint(SprintEntity plan){
        return save(plan);
    }

    public void updateSprint(SprintEntity plan){
        save(plan);
    }

    public void deleteSprint(Long id){
       markAsDeleted(id, SprintEntity.class);
    }

    public List<SprintEntity> findSprints(Long projectId){
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(SprintEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        return find(query, SprintEntity.class);
    }

    public List<SprintEntity> findSprintsByIds(List<Long> ids){
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).in(ids));
        Query query = Query.query(criteria);
        return find(query, SprintEntity.class);
    }

    public SprintEntity findById(Long id) {
        return findById(id, SprintEntity.class);
    }

    public List<SprintEntity> findUnPlanedSprints(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(PlanEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        query.columns(PlanEntity.Fields.sprints);
        List<PlanEntity> planEntities = find(query, PlanEntity.class);
        List<Long> plannedSprints =
                planEntities.stream().flatMap(plan -> plan.getSprints().stream()).collect(Collectors.toList());


        Criteria criteria2 = Criteria.empty();
        criteria2 = criteria2.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria2 = criteria2.and(Criteria.where(SprintEntity.Fields.projectId).is(projectId));
        criteria2 = criteria2.and(Criteria.where(BaseEntity.Fields.id).notIn(plannedSprints));
        Query query2 = Query.query(criteria2);
        return find(query2, SprintEntity.class);
    }

    public void batchCreateSprints(Collection<SprintEntity> sprints) {
        saveAll(sprints);
    }
}
