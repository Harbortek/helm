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

import cn.hutool.db.sql.SqlBuilder;
import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.tracker.entity.link.TrackerLinkEntity;
import com.harbortek.helm.util.CacheUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class TrackerLinkDao extends BaseJdbcDao {
    private final JdbcTemplate jdbcTemplate;

    public TrackerLinkDao(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTrackerLink(TrackerLinkEntity trackerLinkEntity) {
        trackerLinkEntity.setId(IDUtils.getId());
        save(trackerLinkEntity);
    }

    public void updateTrackerLink(TrackerLinkEntity trackerLinkEntity) {
        save(trackerLinkEntity);
    }

    public void deleteTrackerLink(Long id) {
        markAsDeleted(id, TrackerLinkEntity.class);
    }

    public void deleteByIds(List<Long> linkIds) {
        markAdDeleted(linkIds, TrackerLinkEntity.class);
    }

    public List<TrackerLinkEntity> findByItemId(Long itemId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(
                Criteria.where(TrackerLinkEntity.Fields.sourceItemId).is(itemId).or(
                        Criteria.where(TrackerLinkEntity.Fields.targetItemId).is(itemId)));

        criteria = criteria.and(Criteria.where(TrackerLinkEntity.Fields.linkTypeId).isNotNull());
        Query query = Query.query(criteria);
        return find(query, TrackerLinkEntity.class);
    }

    public List<TrackerLinkEntity> findByItemIds(List<Long> odlItemIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(
                Criteria.where(TrackerLinkEntity.Fields.sourceItemId).in(odlItemIds).or(
                        Criteria.where(TrackerLinkEntity.Fields.targetItemId).in(odlItemIds)));
        criteria = criteria.and(
                Criteria.where(TrackerLinkEntity.Fields.linkTypeId).isNotNull());
        Query query = Query.query(criteria);
        return find(query, TrackerLinkEntity.class);
    }

    public List<TrackerLinkEntity> findBySourceItemIds(List<Long> targetItemIds, Long linkTypeId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerLinkEntity.Fields.sourceItemId).in(targetItemIds));
        if (ObjectUtils.isValid(linkTypeId)) {
            criteria = criteria.and(Criteria.where(TrackerLinkEntity.Fields.linkTypeId).is(linkTypeId));
        }
        Query query = Query.query(criteria);
        return find(query, TrackerLinkEntity.class);
    }

    public List<TrackerLinkEntity> findByTargetIds(List<Long> targetItemIds, Long linkTypeId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerLinkEntity.Fields.targetItemId).in(targetItemIds));
        if (ObjectUtils.isValid(linkTypeId)) {
            criteria = criteria.and(Criteria.where(TrackerLinkEntity.Fields.linkTypeId).is(linkTypeId));
        }
        Query query = Query.query(criteria);
        return find(query, TrackerLinkEntity.class);
    }

    public List<TrackerLinkEntity> findBySourceIds(List<Long> sourceItemIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerLinkEntity.Fields.sourceItemId).in(sourceItemIds));
        Query query = Query.query(criteria);
        return find(query, TrackerLinkEntity.class);
    }

    public void batchCreateTrackerLinks(Collection<TrackerLinkEntity> entities) {
        if (entities.isEmpty()) {
            return;
        }
        saveAll(entities);
    }

    public void deleteByItemId(Long itemId) {
//        Criteria criteria = Criteria.empty();
//        criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
//        criteria.and(
//                Criteria.where(TrackerLinkEntity.Fields.sourceItemId).is(itemId).or(
//                        Criteria.where(TrackerLinkEntity.Fields.targetItemId).is(itemId)));
//        Query query = Query.query(criteria);
//        Update update =
//                Update.update(BaseEntity.Fields.deleted, Boolean.TRUE);
//        updateMulti(query, update, TrackerLinkEntity.class);

        Condition condition = DSL.noCondition();
        condition = condition.and(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE));
        condition = condition.and(getField(TrackerLinkEntity.Fields.sourceItemId).eq(itemId).or(getField(TrackerLinkEntity.Fields.targetItemId).eq(itemId)));
        getDslContext().update(getTable(TrackerLinkEntity.class)).set(getField(BaseEntity.Fields.deleted), Boolean.TRUE).where(condition).execute();
    }

    public Long findCountByLinkTypeId(Long linkTypeId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TrackerLinkEntity.Fields.linkTypeId).in(linkTypeId));
        Query query = Query.query(criteria);
        return count(query, TrackerLinkEntity.class);
    }

    public void deleteByItemIds(Collection<Long> itemIds) {
//        Criteria criteria = Criteria.empty();
//        criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
//        criteria.and(
//                Criteria.where(TrackerLinkEntity.Fields.sourceItemId).in(itemIds).or(
//                        Criteria.where(TrackerLinkEntity.Fields.targetItemId).in(itemIds)));
//        Query query = Query.query(criteria);
//        Update update =
//                Update.update(BaseEntity.Fields.deleted, Boolean.TRUE);
//        updateMulti(query, update, TrackerLinkEntity.class);

        Condition condition = DSL.noCondition();
        condition = condition.and(getField(BaseEntity.Fields.deleted).eq(Boolean.FALSE));
        condition = condition.and(getField(TrackerLinkEntity.Fields.sourceItemId).in(itemIds).or(getField(TrackerLinkEntity.Fields.targetItemId).in(itemIds)));
        getDslContext().update(getTable(TrackerLinkEntity.class)).set(getField(BaseEntity.Fields.deleted), Boolean.TRUE).where(condition).execute();
    }

    public void batchDelete(List<TrackerLinkEntity> delLinks) {
        if (delLinks.isEmpty()) {
            return;
        }
        String sql = "UPDATE %s SET %s = true  WHERE %s = ? and %s = ? and %s = ?"
                .formatted(getTable(TrackerLinkEntity.class).getName()
                        , getField(BaseEntity.Fields.deleted).getName()
                        , getField(TrackerLinkEntity.Fields.linkTypeId).getName()
                        , getField(TrackerLinkEntity.Fields.sourceItemId).getName()
                        , getField(TrackerLinkEntity.Fields.targetItemId).getName()
                );
        List<Object[]> batchArgs = delLinks.stream().map((link) -> {
            return new Object[]{link.getLinkTypeId(), link.getSourceItemId(), link.getTargetItemId()};
        }).collect(Collectors.toList());

        int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchArgs);

        CacheUtils.evictAll(TrackerLinkEntity.class);

    }
}
