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

package com.harbortek.helm.smartpage.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.tracker.entity.link.TrackerLinkEntity;
import com.harbortek.helm.tracker.entity.plan.SprintEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class TableTraceabilityDao extends BaseJdbcDao {
    public List<TrackerItemEntity> findTrackerItemsByTrackerId(Long trackerId,Long targetVersionId){
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerItemEntity.Fields.trackerId).is(trackerId));
        if (ObjectUtils.isValid(targetVersionId)){
            Collection<Long> sprintIds = findSprintByTargetVersion(targetVersionId);
            criteria = criteria.and(Criteria.where(TrackerItemEntity.Fields.sprintId).in(sprintIds));
        }
        Query query = Query.query(criteria);
        query.columns(BaseEntity.Fields.id,BaseEntity.Fields.name,BaseEntity.Fields.description,
                               TrackerItemEntity.Fields.trackerId,TrackerItemEntity.Fields.sprintId);

        return find(query,TrackerItemEntity.class);
    }

    public Map<Long,List<TrackerItemEntity>> findLinkedTrackerItems(Collection<Long> targetIds, Long trackerId,
                                                                    Long linkTypeId){
        List<TrackerLinkEntity> links = findLinksByTarget(targetIds, linkTypeId);
        Collection<Long> sourceItemIds = ObjectUtils.ids(links,
                                                   TrackerLinkEntity.Fields.sourceItemId);

        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerItemEntity.Fields.trackerId).is(trackerId));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).in(sourceItemIds));
        Query query =Query.query(criteria);
        query.columns(BaseEntity.Fields.id,BaseEntity.Fields.name,BaseEntity.Fields.description,TrackerItemEntity.Fields.itemNo,
                               TrackerItemEntity.Fields.trackerId,TrackerItemEntity.Fields.sprintId);
        List<TrackerItemEntity> sourceItems = find(query,TrackerItemEntity.class);
        Map<Long,List<TrackerItemEntity>> result = new HashMap<>();
        for(TrackerLinkEntity link : links) {
            List<TrackerItemEntity> items = new ArrayList<>();
            for (TrackerItemEntity item : sourceItems) {
                if (Objects.requireNonNull(item.getId()).equals(link.getSourceItemId())) {
                    items.add(item);
                }
            }
            result.put(link.getTargetItemId(), items);
        }
        return result;
    }

    private List<TrackerLinkEntity> findLinksByTarget(Collection<Long> sourceIds, Long linkTypeId){

        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerLinkEntity.Fields.targetItemId).in(sourceIds));
        criteria = criteria.and(Criteria.where(TrackerLinkEntity.Fields.linkTypeId).is(linkTypeId));
        Query query = Query.query(criteria);
        return find(query, TrackerLinkEntity.class);
    }

    private Collection<Long> findSprintByTargetVersion(Long targetVersionId){
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(SprintEntity.Fields.targetVersionId).is(targetVersionId));
        Query query = Query.query(criteria);
        query.columns(BaseEntity.Fields.id);
        List<SprintEntity> sprints = find(query, SprintEntity.class);
        return ObjectUtils.ids(sprints);
    }
}
