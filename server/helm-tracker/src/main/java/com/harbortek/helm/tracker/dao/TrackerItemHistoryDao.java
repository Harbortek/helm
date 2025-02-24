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
import com.harbortek.helm.common.entity.HistoryBaseEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemHistoryEntity;
import com.harbortek.helm.util.BeanCopyUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Repository
public class TrackerItemHistoryDao extends BaseJdbcDao {

    /**
     * @param projectId
     */
    public List<TrackerItemHistoryEntity> copyByProjectId(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerItemEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        List<TrackerItemEntity> source = find(query, TrackerItemEntity.class);
        return copyMany(source);
    }

    public List<TrackerItemHistoryEntity> copyByIdList(List<Long> ids) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).in(ids));
        Query query = Query.query(criteria);
        List<TrackerItemEntity> source = find(query, TrackerItemEntity.class);

        return copyMany(source);
    }

    public List<TrackerItemHistoryEntity> copyByTracker(Long trackerId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerItemEntity.Fields.trackerId).is(trackerId));
        Query query = Query.query(criteria);
        List<TrackerItemEntity> source = find(query, TrackerItemEntity.class);

        return copyMany(source);
    }


    /**
     * 往历史表中复制revision不同的trackerItem
     */
    public List<TrackerItemHistoryEntity> copyMany(List<TrackerItemEntity> source) {

//        AtomicInteger count = new AtomicInteger();
//        List<String> ids = new ArrayList<>();
        List<Long> itemIds = source.stream().map(BaseEntity::getId).toList();
        Map<Long, TrackerItemHistoryEntity> historyMap = findByObjectIds(itemIds).stream().collect(Collectors
                .toMap(HistoryBaseEntity::getObjectId, Function.identity()));//过滤已存在history
        List<TrackerItemHistoryEntity> existItems=new ArrayList<>();

        List<TrackerItemHistoryEntity> historyList = new ArrayList<>();
        for (TrackerItemEntity item : source) {
            TrackerItemHistoryEntity history = new TrackerItemHistoryEntity();
            BeanCopyUtils.copyWithoutNullProperties(item, history);
            history.setId(IDUtils.getId());
            history.setObjectId(item.getId());
            history.setCreateDate(null);
            if(ObjectUtils.isNotEmpty(historyMap.get(item.getId()))
                && item.getRevision().equals(historyMap.get(item.getId()).getRevision())){
                existItems.add(historyMap.get(item.getId()));
            }else{
                historyList.add(history);
            }
        }
        saveAll(historyList);
        historyList.addAll(existItems);
        return historyList;
    }

    public List<TrackerItemHistoryEntity> copyOne(Long id) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria =criteria.and(Criteria.where(BaseEntity.Fields.id).is(id));
        Query query = Query.query(criteria);
        TrackerItemEntity item = findOne(query, TrackerItemEntity.class);


        Assert.notNull(item, "tracker item must not be null!");
        TrackerItemHistoryEntity history = new TrackerItemHistoryEntity();
        BeanCopyUtils.copyWithoutNullProperties(item, history);
        history.setId(IDUtils.getId());
        history.setObjectId(item.getId());
        history.setCreateDate(null);
        save(history);

        return List.of(history);
    }

    public List<TrackerItemHistoryEntity> findByObjectIds(List<Long> objectIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(HistoryBaseEntity.Fields.objectId).in(objectIds));
        Query query = Query.query(criteria);
        query.columns(BaseEntity.Fields.id,BaseEntity.Fields.name,BaseEntity.Fields.icon,
                HistoryBaseEntity.Fields.objectId,HistoryBaseEntity.Fields.revision);
        return find(query, TrackerItemHistoryEntity.class);
    }

    public List<TrackerItemHistoryEntity> findByHistoryIdsForCompare(List<Long> diffHistoryIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).in(diffHistoryIds));
        Query query = Query.query(criteria);
        query.columns(BaseEntity.Fields.id,BaseEntity.Fields.name,BaseEntity.Fields.icon,
                               HistoryBaseEntity.Fields.objectId,TrackerItemHistoryEntity.Fields.trackerId);
        return find(query, TrackerItemHistoryEntity.class);
    }

    public List<TrackerItemHistoryEntity> findByHistoryIds(List<Long> diffHistoryIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).in(diffHistoryIds));
        Query query = Query.query(criteria);
        return find(query, TrackerItemHistoryEntity.class);
    }

    public List<TrackerItemHistoryEntity> findByProjectId(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerItemHistoryEntity.Fields.projectId).in(projectId));
        Query query = Query.query(criteria);
        return find(query, TrackerItemHistoryEntity.class);
    }

    public void createTrackerItemHistories(List<TrackerItemHistoryEntity> itemHistoryList) {
        saveAll(itemHistoryList);
    }
}
